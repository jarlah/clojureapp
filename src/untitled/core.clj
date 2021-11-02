(ns untitled.core (:require [clj-http.client :as client])(:require [cheshire.core :refer :all]))

(defn get-market-orders [region-id]
  (:body (client/get (str "https://esi.evetech.net/latest/markets/" region-id "/orders") {:accept :json :as :json})))

(defn get-unique-type-ids [orders]
  (distinct (map :type_id orders)))

(defn get-universe-objects
  [type-ids]
  (:body (client/post "https://esi.evetech.net/latest/universe/names" { :body (generate-string type-ids) :as :json :content-type :json })))

(defn get-unique-object-names-sorted [objects]
  (sort (distinct (map :name objects))))

(defn -main []
  (-> (get-market-orders 10000002)
      (get-unique-type-ids)
      (get-universe-objects)
      (get-unique-object-names-sorted)
      (println)))
