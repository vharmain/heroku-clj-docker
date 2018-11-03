(ns example.server
  (:require [clojure.spec.alpha :as s]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as params]
            [spec-tools.core :as st]
            [spec-tools.spec :as spec]))

;; wrap into Spec Records to enable runtime conforming
(s/def ::x spec/int?)
(s/def ::y spec/int?)
(s/def ::total spec/int?)

(s/def ::port spec/int?)
(s/def ::config (s/keys :req-un [::port]))

(def routes
  ["/spec"
   {:coercion reitit.coercion.spec/coercion}
   ["/plus"
    {:responses {200 {:body (s/keys :req-un [::total])}}
     :get
     {:summary    "plus with query-params"
      :parameters {:query (s/keys :req-un [::x ::y])}
      :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                    {:status 200
                     :body   {:total (+ x y)}})}
     :post
     {:summary    "plus with body-params"
      :parameters {:body (s/keys :req-un [::x ::y])}
      :handler    (fn [{{{:keys [x y]} :body} :parameters}]
                    {:status 200
                     :body   {:total (+ x y)}})}}]])

(def app
  (ring/ring-handler
   (ring/router
    routes
    {:data
     {:muuntaja m/instance
      :middleware [params/wrap-params
                   muuntaja/format-middleware
                   coercion/coerce-exceptions-middleware
                   coercion/coerce-request-middleware
                   coercion/coerce-response-middleware]}})
   (ring/create-default-handler)))

(defn start [{:keys [port] :as config}]
  {:pre (s/valid? ::config config)}
  (let [server (jetty/run-jetty #'app {:port port, :join? false})]
    (println "server running in port" port)
    server))



(defn ->int [s]
  (st/coerce spec/int? s st/string-transformer))

(defn -main [& args]
  (let [port (or (->int (System/getenv "PORT")) 3000)]
    (start {:port port})))
