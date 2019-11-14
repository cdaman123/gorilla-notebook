(ns pinkgorilla.storage.storage
  (:require
   [taoensso.timbre :refer-macros (info)]
   ))

(defmulti query-params-to-storage identity)

(defprotocol Storage
  (load-url [self base-path])
  (decode-content [self content]) ; happens after worksheet-content has been loaded via ajax/url fetch
  
  (save-url [self base-path])
  (encode-content [self notebook]) ; goes to body of post request
  
  (external-url [self]) ; to view raw persisted data in browser.
  (gorilla-path [self]) ; to open a notebook from the sidebar
  )
