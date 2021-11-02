(ns untitled.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :refer :all]))

(def base-url "https://esi.evetech.net/latest")

(defn get-market-orders [datasource region-id order-type page]
  (let [url (str base-url "/markets/" region-id "/orders/?datasource=" datasource "&order_type" order-type "&page=" page)]
    (client/get url {:accept :json :as :json :throw-exceptions false})))

(defn get-unique-type-ids [orders]
  (-> (map :type_id orders)
      (distinct)))

(defn get-universe-objects
  [type-ids datasource]
  (let [url (str base-url "/universe/names?datasource=" datasource)]
    (client/post url {:body (generate-string type-ids) :as :json :content-type :json :throw-exceptions false})))

(defn get-unique-object-names-sorted [objects]
  (-> (map :name objects)
      (distinct)
      (sort)))

(defn handle-response [response]
  (if (== 200 (:status response))
    (:body response)
    (println (str "Request failed with status code " (:status response) ": " + (:body response)))))

(defn -main [& [datasource region-id order-type page]]
  (some-> (get-market-orders datasource region-id order-type page)
          (handle-response)
          (get-unique-type-ids)
          (get-universe-objects datasource)
          (handle-response)
          (get-unique-object-names-sorted)
          (println)))
