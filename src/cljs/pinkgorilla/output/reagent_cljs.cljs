(ns pinkgorilla.output.reagent-cljs
  (:require
   [reagent.core :as reagent :refer [atom]]
   [taoensso.timbre :refer-macros (info)]))


(defn output-reagent-cljs
  [output _]
  (let [
        component (:content output)
        _ (info "reagent component: " component)
        ]
    (reagent/create-class
     {:display-name "output-reagent-cljs"
      :reagent-render (fn []
                        [:div.reagent
                         component])})))
