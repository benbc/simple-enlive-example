(ns simple-enlive-example.main
  (:use ring.adapter.jetty
        compojure.core
        compojure.route)
  (:require [compojure.handler :as handler]
            [net.cgrand.enlive-html :as enlive])
  (:gen-class))

(def things ["one" "two" "three" "four"])

(enlive/deftemplate layout "layout.html" [title content]
  [#{:title :h1}] (enlive/content title)
  [:div.content] (enlive/substitute content))

(enlive/defsnippet show "show.html" [:div.content]
  [things]
  [:li] (enlive/clone-for [thing things] (enlive/content thing)))

(def index (enlive/html-resource "index.html"))

(defroutes app-routes
  (GET "/" [] (layout "Front page" index))
  (GET "/show" [] (layout "Show things" (show things)))
  (not-found "Not Found"))

(defn -main []
  (run-jetty (handler/site app-routes) {:port 8080}))
