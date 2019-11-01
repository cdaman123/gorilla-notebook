(ns test
  (:require [fortune.core]))

(defn wisdom []
  [:p "The proof is in the pudding"])

(defn cookie []
  (fortune.core/cookie))