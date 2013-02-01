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

(defmacro defpage
  ([name title source]
     `(def ~name
        (layout ~title
                (enlive/html-resource ~source))))
  ([name title source args & forms]
     `(defn ~name ~args
        (layout ~title
                (enlive/at (enlive/html-resource ~source)
                           ~@forms)))))

(defpage show "Show things" "show.html" [things]
  [:li] (enlive/clone-for [thing things] (enlive/content thing)))

(defpage index "Front page" "index.html")

(defroutes app
  (GET "/" [] index)
  (GET "/show" [] (show things))
  (not-found "Not Found"))

(defn -main []
  (run-jetty (handler/site app) {:port 8080}))
