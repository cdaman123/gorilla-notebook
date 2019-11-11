(ns pinkgorilla.kernel.cljs-tools)

(defn r! [vec_or_reagent_f]
  {:value-response
   {:type "reagent" ; "reagent-hydrated"
    :content vec_or_reagent_f
      ;:value result
    }})
