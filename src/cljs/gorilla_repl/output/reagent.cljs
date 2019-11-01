(ns gorilla-repl.output.reagent
  (:require
   [reagent.core :as reagent :refer [atom]]
   [widget.core :refer [resolve-function]]
   ))








(defn resolve-vector [x]
  (let [;_ (println "reagent function found: " x)
        ;_ (println "type of arg: " (type (first (rest x))))
        a [(resolve-function (first x))]
        b (into [] (assoc x 0 (resolve-function (first x))))
        ;b (into [] (assoc x 0 :h1))
        ]
    ;(println "a is: " a)
    ;(println "b is: " b)
    
    ;a
    b
    ))


(def state-atom
  (reagent/atom 
   {:name "bongo" 
    :time "5 before 12"}))



(defn resolve-functions
  "resolve function-as symbol to function references in the reagent-hickup-map.
   Leaves regular hiccup data unchanged."
  [reagent-hiccup-syntax]
  (clojure.walk/prewalk
    (fn [x] 
      (if (and (coll? x) (symbol? (first x)))
          (resolve-vector x)
          x))
    reagent-hiccup-syntax))

(defn resolve-state [x]
  (println "found :widget-state: " x)
  state-atom
  )

(defn resolve-atoms
  "resolve function-as symbol to function references in the reagent-hickup-map.
   Leaves regular hiccup data unchanged."
  [reagent-hiccup-syntax]
  (clojure.walk/prewalk
   (fn [x]
     (if (= x :widget-state) 
        (resolve-state x)
       x))
   reagent-hiccup-syntax))


(defn output-reagent
  [output _]
  (let [content (:content output)
        component (cljs.reader/read-string (:value output))
        _ (println "reagent component: " component)
        component (resolve-functions component)
        component (resolve-atoms component)
        ;_ (println "resolved component: " component)
        ;initial-state (:initial-state content)
        ;state (reagent/atom initial-state)
        
        ;widget (name-to-reagent widget-name)
        ]
        (reagent/create-class 
         {:display-name "output-reagent"
          :reagent-render (fn []
                            [:div.reagent
                             component
                             [:p (str "state: " @state-atom)]]
                             )})))
