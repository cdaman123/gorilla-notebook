(ns pinkgorilla.storage.core
  (:require
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.routes :as routes]
   
   [pinkgorilla.storage.local]
   [pinkgorilla.storage.gist]
   [pinkgorilla.storage.repo]
   [pinkgorilla.storage.bitbucket]))

;(defn nav-local! [filename]
;   (routes/nav! (str "/edit?worksheet-filename=" filename)))



