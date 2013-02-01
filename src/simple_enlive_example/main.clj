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

(enlive/deftemplate layout "layout.html" [title content]
  [#{:title :h1}] (enlive/content title)
  [:div.content] (enlive/substitute (extract-body content)))

(defmacro defpage [name source args & forms]
  `(defn ~name ~args
     (enlive/at (enlive/html-resource ~source)
                ~@forms)))

(defpage show "show.html" [things]
  [:li] (enlive/clone-for [thing things] (enlive/content thing)))

(defpage index "index.html" [])

(defroutes app
  (GET "/" [] (layout "Front page" (index)))
  (GET "/show" [] (layout "Show things" (show things)))
  (not-found "Not Found"))

(defn -main []
  (run-jetty (handler/site app) {:port 8080}))
