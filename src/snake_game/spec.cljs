(ns snake-game.spec
    (:require
     [cljs.spec :as s]
     [snake-game.utils :as utils]
     [clojure.test.check.generators :as gen]))

(defn only-one [[n1 n2 & xs]]
  (if (not (nil? n2))
    (if (< (Math/abs (- n1 n2)) 2)
      (cons true (only-one (cons n2 xs)))
      [false])
    []))

(defn kropp? [kropp]
  (let [xs (map first kropp)
        ys (map second kropp)]
    (and (every? identity (only-one xs))
         (every? identity (only-one ys)))))

(s/def ::naturlig-tall     (s/and integer? #(< -1 %)))
(s/def ::x                 (s/and ::naturlig-tall #(< % 36)))
(s/def ::y                 (s/and ::naturlig-tall #(< % 26)))
(s/def ::koordinat-system  (s/tuple ::x ::y))
(s/def ::punkt             (s/tuple ::x ::y))
(s/def ::brett             ::koordinat-system)
(s/def ::retning           (s/and (s/tuple #{-1 0 1} #{-1 0 1}) (fn [[x y]] (not= x y))))
(s/def ::kropp             (s/and (s/coll-of ::punkt []) kropp?))
(s/def ::skatt             ::punkt)
(s/def ::poeng             (s/and integer? #(< -1 %)))
(s/def ::er-spillet-igang? #{true false})
(s/def ::slange            (s/keys :req-un [::retning ::kropp]))
(s/def ::spill             (s/keys :req-un [::brett ::slange ::skatt ::poeng ::er-spillet-igang?]))

(s/fdef utils/alle-plasser-på-brettet
        :args (s/cat :brett ::brett)
        :ret (s/coll-of ::punkt []))

(s/fdef utils/alle-ledige-plasser
        :args (s/cat :slangens-plasser (s/coll-of ::punkt []) :ledige-plassser (s/coll-of ::punkt []))
        :ret (s/coll-of ::punkt []))

(s/fdef utils/finn-en-tilfeldig-ledig-plass-på-brettet
        :args (s/cat :slange ::slange :brett ::brett)
        :ret ::punkt)

(s/fdef utils/er-det-en-kollisjon?
        :args (s/cat :slange ::slange :brett ::brett)
        :ret #{true false})

(s/fdef utils/slange-halen
        :args (s/cat :punkt ::punkt)
        :ret integer?)

(s/fdef utils/gjør-slangen-større
        :args (s/cat :slange ::slange)
        :fn #(= (-> % :args :slange :kropp count inc) (-> % :ret :slange :kropp count))
        :ret ::slange)

(s/fdef utils/har-vi-truffet-skatten?
        :args (s/cat :slange ::slange :skatt ::punkt)
        :ret #{true false})

(s/fdef utils/utfør-flytt
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef utils/avslutt
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef utils/lag-ny-slange
        :args (s/cat :slange ::slange :nytt-hode ::punkt)
        :fn #(and (= (-> % :args :slange :kropp count) (-> % :ret :kropp count))
                  (= (-> % :args :slange :kropp first) (-> % :ret :kropp second))
                  (= (-> % :args :slange :retning) (-> % :ret :retning)))
        :ret ::slange)

(s/fdef utils/lag-nytt-hode
        :args (s/cat :slange ::slange)
        :ret ::punkt)

(s/fdef utils/flytt-slangen
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef utils/neste-steg
        :args (s/cat :spill ::spill)
        :ret ::spill)

(s/fdef utils/oppdater-spill
        :args (s/cat :spill ::spill :foo keyword?)
        :ret ::spill)

(s/fdef utils/bytt-retning-på-slangen
        :args (s/cat :ny-retning ::retning :gammel-retning ::retning)
        :ret ::retning)

(s/fdef utils/endre-retning
        :args (s/cat :spill ::spill :event (s/tuple (s/and keyword? #(= :endre-retning %)) ::retning))
        :ret ::spill)

(s/fdef utils/start-spill
        :args (s/cat :spill ::spill :foo keyword?)
        :ret ::spill)
