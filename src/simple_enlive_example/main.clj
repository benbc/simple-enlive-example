(ns simple-enlive-example.main
  (:use ring.adapter.jetty
        compojure.core
        compojure.route)
  (:require [compojure.handler :as handler]
            [net.cgrand.enlive-html :as enlive])
  (:gen-class))

(def things ["one" "two" "three" "four"])

(defn extract-body [html]
  (enlive/at html [#{:html :body}] enlive/unwrap))

(defmacro deflayout [name source args & forms]
  `(defn ~name ~args
     (enlive/template ~source [content#]
                      [:div.content] (enlive/substitute (extract-body content#))
                      ~@forms)))

(defmacro defpage
  ([name source layout]
     `(def ~name
        (~layout (enlive/html-resource ~source))))
  ([name source layout args & forms]
     `(defn ~name ~args
        (~layout (enlive/at (enlive/html-resource ~source)
                            ~@forms)))))

(deflayout default-layout "layout.html" [title]
  [#{:title :h1}] (enlive/content title))

(defpage show "show.html" (default-layout "Show things") [things]
  [:li] (enlive/clone-for [thing things] (enlive/content thing)))

(defpage index "index.html" (default-layout "Front page"))

(defroutes app
  (GET "/" [] index)
  (GET "/show" [] (show things))
  (not-found "Not Found"))

(defn -main []
  (run-jetty (handler/site app) {:port 8080}))
