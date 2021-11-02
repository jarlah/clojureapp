(ns untitled.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :refer :all]))

(def base-url "https://esi.evetech.net/latest")

(defn get-market-orders [region-id]
  (client/get (str base-url "/markets/" region-id "/orders")
              {:accept :json :as :json :throw-exceptions false}))

(defn get-unique-type-ids [orders]
  (-> (map :type_id orders)
      (distinct)))

(defn get-universe-objects
  [type-ids]
  (client/post (str base-url "/universe/names")
               {:body (generate-string type-ids) :as :json :content-type :json :throw-exceptions false}))

(defn get-unique-object-names-sorted [objects]
  (-> (map :name objects)
      (distinct)
      (sort)))

(defn handle_response [response]
  (if (== 200 (:status response))
    (:body response)
    (println (str "Request failed with status code " (:status response) ": " + (:body response)))))

(defn -main []
  (some-> (get-market-orders 10000002)
          (handle_response)
          (get-unique-type-ids)
          (get-universe-objects)
          (handle_response)
          (get-unique-object-names-sorted)
          (println)))
