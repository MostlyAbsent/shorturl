(ns shorturl.core
  (:require [ring.adapter.jetty :as ring-jetty]
            [reitit.ring :as ring]
            [ring.util.response :as r]
            [muuntaja.core :as m]
            [shorturl.db :as db]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [shorturl.slug :refer [generate-slug]]
            [clojure.java.io :as io])
  (:gen-class))

(defn index []
  (slurp (io/resource "public/index.html")))

(defn redirect [req]
  (let [slug (get-in req [:path-params :slug])
        url (db/get-url slug)]
    (if url
      (r/redirect url 307)
      (r/not-found "Not found"))))

(defn create-redirect [req]
  (let [url (get-in req [:body-params :url])
        slug (generate-slug)]
    (if url
      (do (db/insert-redirect! slug url)
          (r/response {:slug slug}))
      (r/bad-request "No url"))))

(def app
  (ring/ring-handler
   (ring/router
    ["/" {:no-doc true}
     [":slug/" redirect]
     ["api/"
      ["redirect/" {:post create-redirect}]]
     ["assets/*" (ring/create-resource-handler {:root "public/assets"})]
     ["" {:handler (fn [req] {:body (index) :status 200})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty #'app {:port 3000
                             :join? false}))

(defn -main []
  (println "Starting app")
  (start))
