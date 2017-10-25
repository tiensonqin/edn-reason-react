let optBoolToOptJsBoolean =
  fun
  | None => None
  | Some v => Some (Js.Boolean.to_js_boolean v);

let unwrapBool v => Js.Undefined.from_opt @@ optBoolToOptJsBoolean v;

module Button = {
  external button : ReasonReact.reactClass = "antd/lib/button" [@@bs.module];
  let make ::type_=? ::htmlType=? ::icon=? ::shape=? ::size=? ::loading=? ::onClick=? ::ghost=? =>
    ReasonReact.wrapJsForReason
      reactClass::button
      props::
        Js.Undefined.(
          {
            "type": from_opt type_,
            "htmlType": from_opt htmlType,
            "icon": from_opt icon,
            "shape": from_opt shape,
            "size": from_opt size,
            "loading": from_opt loading,
            "onClick": from_opt onClick,
            "ghost": from_opt ghost
          }
        );
};

module Icon = {
  external icon : ReasonReact.reactClass = "antd/lib/icon" [@@bs.module];
  let make ::type_=? ::className=? ::spin=? ::style=? ::onClick=? =>
    ReasonReact.wrapJsForReason
      reactClass::icon
      props::
        Js.Undefined.(
          {
            "type": from_opt type_,
            "className": from_opt className,
            "spin": from_opt spin,
            "style": from_opt style,
            "onClick": from_opt onClick
          }
        );
};

module Row = {
  external row : ReasonReact.reactClass = "antd/lib/row" [@@bs.module];
  let make ::type_=? ::className=? ::gutter=? ::glign=? ::justify=? ::style=? =>
    ReasonReact.wrapJsForReason
      reactClass::row
      props::
        Js.Undefined.(
          {
            "type": from_opt type_,
            "className": from_opt className,
            "gutter": from_opt gutter,
            "glign": from_opt glign,
            "justify": from_opt justify,
            "style": from_opt style
          }
        );
};

module Col = {
  external col : ReasonReact.reactClass = "antd/lib/col" [@@bs.module];
  let make
      ::className=?
      ::span=?
      ::order=?
      ::offset=?
      ::push=?
      ::pull=?
      ::xs=?
      ::sm=?
      ::md=?
      ::lg=?
      ::xl=?
      ::style=? =>
    ReasonReact.wrapJsForReason
      reactClass::col
      props::
        Js.Undefined.(
          {
            "className": from_opt className,
            "span": from_opt span,
            "order": from_opt order,
            "offset": from_opt offset,
            "push": from_opt push,
            "pull": from_opt pull,
            "xs": from_opt xs,
            "sm": from_opt sm,
            "md": from_opt md,
            "lg": from_opt lg,
            "xl": from_opt xl,
            "style": from_opt style
          }
        );
};

module Layout = {
  external layout : ReasonReact.reactClass = "antd/lib/layout" [@@bs.module];
  let make ::className=? ::style=? =>
    ReasonReact.wrapJsForReason
      reactClass::layout
      props::Js.Undefined.({"className": from_opt className, "style": from_opt style});
  module Header = {
    external header : ReasonReact.reactClass = "Header" [@@bs.module "antd/lib/layout"];
    let make ::className=? ::style=? =>
      ReasonReact.wrapJsForReason
        reactClass::header
        props::Js.Undefined.({"className": from_opt className, "style": from_opt style});
  };
  module Footer = {
    external footer : ReasonReact.reactClass = "Footer" [@@bs.module "antd/lib/layout"];
    let make ::className=? ::style=? =>
      ReasonReact.wrapJsForReason
        reactClass::footer
        props::Js.Undefined.({"className": from_opt className, "style": from_opt style});
  };
  module Content = {
    external content : ReasonReact.reactClass = "Content" [@@bs.module "antd/lib/layout"];
    let make ::className=? ::style=? =>
      ReasonReact.wrapJsForReason
        reactClass::content
        props::Js.Undefined.({"className": from_opt className, "style": from_opt style});
  };
  module Sider = {
    external sider : ReasonReact.reactClass = "Sider" [@@bs.module "antd/lib/layout"];
    let make
        ::className=?
        ::collapsible=?
        ::collapsed=?
        ::defaultCollapsed=?
        ::reverseArrow=?
        ::onCollapse=?
        ::trigger=?
        ::width=?
        ::collapsedWidth=?
        ::breakpoint=?
        ::style=? =>
      ReasonReact.wrapJsForReason
        reactClass::sider
        props::
          Js.Undefined.(
            {
              "className": from_opt className,
              "collapsible": unwrapBool collapsible,
              "collapsed": unwrapBool collapsed,
              "defaultCollapsed": unwrapBool defaultCollapsed,
              "reverseArrow": unwrapBool reverseArrow,
              "onCollapse": from_opt onCollapse,
              "trigger": from_opt trigger,
              "width": from_opt width,
              "collapsedWidth": from_opt collapsedWidth,
              "breakpoint": from_opt breakpoint,
              "style": from_opt style
            }
          );
  };
};

module Menu = {
  external menu : ReasonReact.reactClass = "antd/lib/menu" [@@bs.module];
  let make ::className=? ::theme=? =>
    ReasonReact.wrapJsForReason
      reactClass::menu
      props::Js.Undefined.({"className": from_opt className, "theme": from_opt theme});
  module Divider = {
    external divider : ReasonReact.reactClass = "Divider" [@@bs.module "antd/lib/menu"];
    let make ::className=? ::disabled=? =>
      ReasonReact.wrapJsForReason
        reactClass::divider
        props::Js.Undefined.({"className": from_opt className, "disabled": unwrapBool disabled});
  };
  module Item = {
    external item : ReasonReact.reactClass = "Item" [@@bs.module "antd/lib/menu"];
    let make ::disabled=? ::key=? =>
      ReasonReact.wrapJsForReason
        reactClass::item
        props::Js.Undefined.({"disabled": unwrapBool disabled, "key": from_opt key});
  };
  module SubMenu = {
    external subMenu : ReasonReact.reactClass = "SubMenu" [@@bs.module "antd/lib/menu"];
    let make ::disabled=? ::key=? ::title=? ::children=? ::onTitleClick=? =>
      ReasonReact.wrapJsForReason
        reactClass::subMenu
        props::
          Js.Undefined.(
            {
              "disabled": unwrapBool disabled,
              "key": from_opt key,
              "title": from_opt title,
              "children": from_opt children,
              "onTitleClick": from_opt onTitleClick
            }
          );
  };
  module ItemGroup = {
    external itemGroup : ReasonReact.reactClass = "ItemGroup" [@@bs.module "antd/lib/menu"];
    let make ::title=? ::children=? =>
      ReasonReact.wrapJsForReason
        reactClass::itemGroup
        props::Js.Undefined.({"title": from_opt title, "children": from_opt children});
  };
};
