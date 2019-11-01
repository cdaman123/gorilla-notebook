(ns widget.core
    (:require 
     ; This widgets should be compiled into the bundle.
     ; TODO: Make this list dynamic (depending on the notebook requirements)
     [widget.clock]
     [widget.combo]
     [widget.hello]
     [widget.text]
     
     [re-com.core
      :as rcm
      :refer [h-box v-box box gap line h-split v-split scroller
              button row-button md-icon-button md-circle-icon-button info-button
              input-text input-password input-textarea
              label title p
              single-dropdown
              checkbox radio-button slider progress-bar throbber
              horizontal-bar-tabs vertical-bar-tabs
              modal-panel popover-content-wrapper popover-anchor-wrapper]]
     
     ))


(defn widget-not-found
  [name]
  [:div.widget-not-found {:style {:background-color "red"}}
   [:h3 "WIDGET NOT FOUND!"]
   [:p "You need to specify the fully-qualified name of the widget"]
   [:p "Example: widget.hello/world"]
   [:p "Example: widget.clock/binary-clock"]
   [:p (str "You have entered: " name)]])



;(defn resolve-function [s]
;  (let [_ (println "resolving function " s)  ;  (resolve 'bar.core/woz)
;        ;f (resolve-f s)
;        f (cljs.core/resolve s)
;        _ (if (nil? f) (println "Sorry - Not Found!"))]
;    (if (nil? f)
;      widget-not-found
;      f)))



(defn resolve-function [s]
  (let [;gorilla-repl.output.reagentwidget (cljs.core/resolve (symbol widget-name)) ; this is what we want, but resolve is a macro
        _ (println "resolving-function " s)]
    (case s
      widget.clock/binary-clock widget.clock/binary-clock
      "widget.combo/list-selector" widget.combo/list-selector
      widget.hello/world widget.hello/world
      widget.hello/love widget.hello/love
      text widget.text/atom-text
      
      rcm/button rcm/button
      rcm/box rcm/box
      rcm/h-box rcm/h-box
      rcm/v-box rcm/v-box

      rcm/input-password rcm/input-password
      rcm/slider rcm/slider
      rcm/progress-bar progress-bar
       
      
      widget-not-found)))