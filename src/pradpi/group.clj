(ns pradpi.group
  "Functions for handling the ResponseGroup parameter

  This is mainly to meet a (sane) requirement of the naive-xml
  package. Since XML is so rad, we have to type hint which nodes
  are expected to be lists")

(defn- add-path-if-group
  "Adds the given path to :list-paths if the given group is in :ResponseGroup"
  [params config group path]
  (if (.contains (get params :ResponseGroup "Small") group)
    (update config :list-paths
            #(conj % path))
    config))

(defn- configure-offers
  "If the response group contains offers - flag them as a list type"
  [params config]
  (add-path-if-group params
                     config
                     "Offers"
                     [:items :item :offers :offer]))

(defn- configure-upcs
  "ItemAttributes can contain a list of upc elements"
  [params config]
  (add-path-if-group params
                     config
                     "ItemAttributes"
                     [:items :item :item-attributes :upc-list :upc-list-element]))

(defn xml-config
  "naive-xml requires a hint at which nodes are lists"
  [params]
  (let [conf {:list-paths [[:items :item]]}
        transforms [configure-offers configure-upcs]
        parameterized (map #(partial % params) transforms)
        applied (apply comp parameterized)]
    (applied conf)))
