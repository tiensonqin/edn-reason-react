(ns edn-reason-react.core
  (:require [cljs.reader :as reader]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [goog.string :as gstring]
            [goog.string.format]
            [goog.object :as gobj]))

(nodejs/enable-util-print!)
(def fs (js/require "fs"))

(def child-process (js/require "child_process"))
(def exec-sync (gobj/get child-process "execSync"))

(defn capitalize
  [s]
  (str (.toUpperCase (.charAt s 0))
       (.slice s 1)))

(defn lower-case
  [s]
  (str (.toLowerCase (.charAt s 0))
       (.slice s 1)))

(defn read-edn [path f]
  (.readFile fs path "utf8" (fn [err data] (f path (reader/read-string data)))))

(defn reserved [prop]
  (if (contains? #{"type"} prop)
    (str (name prop) "_")
    prop))

(defn flatten-props [props]
  (map (fn [prop]
         (-> (if (and (map? prop) (= :bool (last (first prop))))
               (name (ffirst prop))
               (name prop))
             (reserved))) props))

(defn generate-args [props]
  (->> (map #(str "::" % "=?") (flatten-props props))
       (str/join " ")))

(defn generate-props [props]
  (->>
   (for [prop props]
     (if (and (map? prop) (= :bool (last (first prop))))                   ; boolean
       (let [prop (name (ffirst prop))]
         (gstring/format "\"%s\": unwrapBool %s" prop (reserved prop)))
       (let [prop (name prop)]
         (gstring/format "\"%s\": from_opt %s" prop (reserved prop)))))
   (str/join ",\n")
   (gstring/format "{%s}")))


;; \"%s\" [@@bs.module];
(defn generate-module [module spec child?]
  (let [module (name module)]
    (gstring/format
     "module %s = {
  external %s : ReasonReact.reactClass = %s;
  let make %s =>
    ReasonReact.wrapJsForReason
      reactClass::%s
      props::Js.Undefined.(%s);
  %s
};
"
     (capitalize module)
     (lower-case module)
     (if child?
       (gstring/format "\"%s\" [@@bs.module \"%s\"]" (capitalize module) (:dir spec))
       (gstring/format "\"%s\" [@@bs.module]" (:dir spec)))
     (generate-args (:props spec))
     (lower-case module)
     (generate-props (:props spec))
     (if (:sub-modules spec)
       (->> (for [[module props] (:sub-modules spec)]
              (generate-module module {:props props
                                       :dir (:dir spec)} true))
            (str/join "\n"))
       ""))))

(defn process [path result]
  ;; helpers

  (let [result (->> (for [[module spec] result]
                      (generate-module module spec false))
                    (str/join "\n")
                    (str "let optBoolToOptJsBoolean =
  fun
  | None => None
  | Some v => Some (Js.Boolean.to_js_boolean v);

  let unwrapBool v => Js.Undefined.from_opt @@ optBoolToOptJsBoolean v;

"))
        write-path (str/replace path ".edn" ".re")]
    (.writeFile fs write-path result (fn [err]
                                       (if err
                                         (println "Error: " err)
                                         (do
                                           (println "Saved in" write-path ".")
                                           ;; run refmt
                                           (exec-sync (str "refmt --in-place " write-path))
                                           )))))


  ;; the entry
  )

(defn -main [& args]
  (let [path (first args)]
    (read-edn path process)))

(set! *main-cli-fn* -main)
