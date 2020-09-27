(ns final
  (:require
    [zero-one.geni.core :as g]
    [zero-one.geni.ml :as ml]
    [zero-one.geni.rdd :as rdd]
    [zero-one.geni.streaming :as streaming]))

(def invoices
  (g/->kebab-columns (g/read-csv! "data/online_retail_ii")))

(def descriptors
  (-> invoices
      (g/remove (g/null? :description))
      (ml/transform
        (ml/tokeniser {:input-col  :description
                       :output-col :descriptors}))
      (ml/transform
        (ml/stop-words-remover {:input-col  :descriptors
                                :output-col :cleaned-descriptors}))
      (g/with-column :descriptor (g/explode :cleaned-descriptors))
      (g/with-column :descriptor (g/regexp-replace :descriptor
                                                   (g/lit "[^a-zA-Z'']")
                                                   (g/lit "")))
      (g/remove (g/< (g/length :descriptor) 3))
      g/cache))

(def log-spending
  (-> descriptors
      (g/remove (g/||
                  (g/null? :customer-id)
                  (g/< :price 0.01)
                  (g/< :quantity 1)))
      (g/group-by :customer-id :descriptor)
      (g/agg {:log-spend (g/log1p (g/sum (g/* :price :quantity)))})
      (g/order-by (g/desc :log-spend))))

(def als-pipeline
  (ml/pipeline
    (ml/string-indexer {:input-col  :descriptor
                        :output-col :descriptor-id})
    (ml/als {:max-iter    50
             :reg-param   0.01
             :user-col    :customer-id
             :item-col    :descriptor-id
             :rating-col  :log-spend})))

(def als-pipeline-model
  (ml/fit log-spending als-pipeline))

(def als-model
  (last (ml/stages als-pipeline-model)))

(ml/recommend-items als-model 3)
