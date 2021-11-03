(ns untitled.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :refer :all])
  (:gen-class))

(def base-url "https://esi.evetech.net/latest")

(defn get-market-orders [datasource region-id order-type page]
  (let [url (str base-url "/markets/" region-id "/orders")
        query-params {:datasource datasource :order_type order-type :page page}]
    (client/get url {:query-params     query-params
                     :accept           :json
                     :as               :json
                     :throw-exceptions false})))

(defn get-unique-type-ids [orders]
  (-> (map :type_id orders)
      (distinct)))

(defn get-universe-objects
  [type-ids datasource]
  (let [url (str base-url "/universe/names")
        body (generate-string type-ids)
        query-params {:datasource datasource}]
    (client/post url {:query-params     query-params
                      :body             body
                      :as               :json
                      :content-type     :json
                      :throw-exceptions false})))

(defn get-unique-object-names-sorted [objects]
  (-> (map :name objects)
      (distinct)
      (sort)))

(defn handle-response [response]
  (if (== 200 (:status response))
    (:body response)
    (println (str "Request failed with status code " (:status response) ": " + (:body response)))))

(defn get-names [datasource region-id order-type page]
  (some-> (get-market-orders datasource region-id order-type page)
          (handle-response)
          (get-unique-type-ids)
          (get-universe-objects datasource)
          (handle-response)
          (get-unique-object-names-sorted)))

(defn -main [& [datasource region-id order-type page]]
  (do
    (println "Program start")
    (let [names (get-names datasource region-id order-type page)]
      (if (some? names)
        (do
          (run! println names)
          (println (str "Found " (count names) " unique objects")))
        (println "Something failed")))
    (println "Program end")))

