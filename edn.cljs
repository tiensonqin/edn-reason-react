#!/usr/bin/env lumo
(ns edn-reason-react.core
  (:require [cljs.reader :as reader]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.set :as set]
            [goog.string :as gstring]
            [goog.string.format]
            [goog.object :as gobj]))

(nodejs/enable-util-print!)
(def fs (js/require "fs"))

(def child-process (js/require "child_process"))
(def exec-sync (gobj/get child-process "execSync"))

(defn strip-underscore
  [s]
  (cond (= (.charAt s 0) "_") (.slice s 1) :else s))

(defn capitalize
  [s]
    (str (.toUpperCase (.charAt (strip-underscore s) 0))
       (.slice (strip-underscore s) 1)))

(defn lower-case
  [s]
  (str (.toLowerCase (.charAt s 0))
       (.slice s 1)))

(defn read-edn [path f save-path]
  (.readFile fs path "utf8" (fn [err data] (f path save-path (reader/read-string data)))))

(defn reserved [prop]
  (if (contains? #{"type" "lazy" "open"} prop)
    (str (name prop) "_")
    (name prop)))

(defn flatten-props [props]
  (map (fn [prop]
         (-> (cond (and (map? prop) (= :bool (last (first prop))))
                   (name (ffirst prop))

                   (and (vector? prop) (= :rename (second prop)))
                   (name (last prop))

                   :else
                   (name prop))
             (reserved))) props))

(defn generate-args [props]
  (->> (map #(str "~" % "=?") (flatten-props props))
       (str/join ",")))

(defn generate-props [props]
  (->>
   (for [prop props]
     (cond
       (and (map? prop) (= :bool (last (first prop)))) ; boolean
       (let [prop (name (ffirst prop))]
         (gstring/format "\"%s\": unwrapBool(%s)" prop (reserved prop)))

       (and (vector? prop) (= :rename (second prop))) ; rename
       (let [original-prop (name (first prop))]
         (gstring/format "\"%s\": fromOption(%s)" original-prop (reserved (last prop))))

       :else
       (let [prop (name prop)]
         (gstring/format "\"%s\": fromOption(%s)" prop (reserved prop)))))
   (str/join ",\n")
   (gstring/format "{%s}")))

(defn generate-module [module spec child?]
  (let [module (name module)
        spec (update spec :props (fn [props]
                                   (if props (set/union props #{:id :className :style}))))]
    (if (:props spec)
      (gstring/format
       "module %s = {
  %s external %s : ReasonReact.reactClass = %s;
  [%bs.raw {|require('%s/style')|}];
  let make = (%s) =>
    ReasonReact.wrapJsForReason(
      ~reactClass=%s,
      ~props=Js.Undefined.(%s)
    );
  %s
  %s
};
"
       (capitalize module)
       (if child?
         (gstring/format "[@bs.module \"%s\"]" (:dir spec))
         (gstring/format "[@bs.module]"))
       (lower-case module)
       (if child?
         (gstring/format "\"%s\"" (capitalize module))
         (gstring/format "\"%s\"" (:dir spec)))
       (:dir spec)
       (generate-args (:props spec))
       (lower-case module)
       (generate-props (:props spec))
       (if (:raw spec) (str "\n" (:raw spec) "\n") "")
       (if (:sub-modules spec)
         (->> (for [[module props] (:sub-modules spec)]
                (generate-module module {:props props
                                         :dir (:dir spec)} true))
              (str/join "\n"))
         ""))
      (gstring/format
       "module %s = {
 %s
 %s
};
"
       (capitalize module)
       (if (:raw spec) (str "\n" (:raw spec) "\n") "")
       (if (:sub-modules spec)
         (->> (for [[module props] (:sub-modules spec)]
                (generate-module module {:props props
                                         :dir (:dir spec)} true))
              (str/join "\n"))
         "")))))

(defn process [path save-path result]
  ;; helpers

  (let [result (->> (for [[module spec] result]
                      (generate-module module spec false))
                    (str/join "\n")
                    (str "let unwrapBool = v => Js.Undefined.fromOption(v);"))
        write-path (if save-path save-path (str/replace path ".edn" ".re"))]
    (.writeFile fs write-path result (fn [err]
                                       (if err
                                         (println "Error: " err)
                                         (do
                                           (println "Saved in" write-path ".")
                                           ;; run refmt
                                           (exec-sync (str "refmt --in-place " write-path))
                                           ))))))

(defn -main [& args]
  (let [path (first args)
        save-path (second args)]
    (read-edn path process save-path)))

(set! *main-cli-fn* -main)
