(ns snake-game.utils
    (:require [snake-game.norwegian :refer [finnes-i? endre-i tall fjern
                                            velg-en-tilfeldig
                                            første-i neste-i
                                            første-i-første
                                            andre-i
                                            kast-siste-i
                                            putt-inni
                                            ta-de-siste
                                            legg-til
                                            legg-til-i
                                            legg-til-inni
                                            øk-med-en
                                            minsk-med-en]]))

(defn alle-plasser-på-brettet [x y]
  (for [x-pos (tall x)
        y-pos (tall y)]
    [x-pos y-pos]))

(defn alle-ledige-plasser [slangens-plasser alle-plasser]
  (fjern slangens-plasser alle-plasser))

(defn finn-en-tilfeldig-ledig-plass-på-brettet [slange [x y]]
  (let [slangens-plasser (putt-inni #{} (:kropp slange))
        alle-plasser (alle-plasser-på-brettet x y)]
    (when-let [ledige-plasser (seq (alle-ledige-plasser slangens-plasser alle-plasser))]
      (velg-en-tilfeldig ledige-plasser))))

(def brett [35 25])

(def slange {:retning [1 0]
             :kropp [[3 2] [2 2] [1 2] [0 2]]})

(def nytt-spill {:brett brett
                 :slange slange
                 :skatt (finn-en-tilfeldig-ledig-plass-på-brettet slange brett)
                 :poeng 0
                 :er-spillet-igang? true})

(defn er-det-en-kollisjon? [{:keys [kropp retning]} [x y]]
  (let [kant-x #{x -1}
        kant-y #{y -1}
        neste-x (+ (første-i retning) (første-i-første kropp))
        neste-y (+ (andre-i retning) (andre-i (første-i kropp)))]
    (or (finnes-i? kant-x neste-x)
        (finnes-i? kant-y neste-y)
        (finnes-i? (putt-inni #{} (rest kropp)) [neste-x neste-y]))))

(defn slange-halen [koordinat-1 koordinat-2]
  (if (= koordinat-1 koordinat-2)
    koordinat-1
    (if (> koordinat-1 koordinat-2)
      (minsk-med-en koordinat-2)
      (øk-med-en koordinat-2))))

(defn gjør-slangen-større [{:keys [kropp retning] :as slange}]
  (let [[[første-x første-y] [andre-x andre-y]] (ta-de-siste 2 kropp)
        x (slange-halen første-x andre-x)
        y (slange-halen første-y andre-y)]
    (-> slange
        (endre-i [:kropp] #(conj % [x y])))))

(defn vi-har-truffet-skatten? [slange skatt]
  (= skatt (første-i (:kropp slange))))

(defn utfør-flytt [{:keys [slange skatt brett] :as spill}]
  (if (vi-har-truffet-skatten? slange skatt)
    (-> spill
        (endre-i [:slange] gjør-slangen-større)
        (endre-i [:poeng]  øk-med-en)
        (legg-til-i :skatt (finn-en-tilfeldig-ledig-plass-på-brettet slange brett)))
    spill))

(defn avslutt [spill]
  (legg-til-i spill :er-spillet-igang? false))

(defn lag-ny-slange [{:keys [kropp] :as slange} nytt-hode]
  (legg-til-i slange :kropp (putt-inni [] (kast-siste-i
                                           (legg-til nytt-hode kropp)))))

(defn lag-nytt-hode [slange]
  (let [retning (:retning slange)
        kropp (:kropp slange)]
    (mapv #(+ %1 %2) retning (første-i kropp))))

(defn flytt-slangen [spill]
  (let [slange (:slange spill)
        nytt-hode (lag-nytt-hode slange)
        ny-slange (lag-ny-slange slange nytt-hode)]
    (legg-til-i spill :slange ny-slange)))

(defn neste-steg [{:keys [slange brett] :as spill}]
  (if (er-det-en-kollisjon? slange brett)
    (avslutt spill)
    (-> spill
        (flytt-slangen)
        (utfør-flytt))))

(defn bytt-retning-på-slangen [[ny-x ny-y] [x y]]
  (if (or (= x ny-x)
          (= y ny-y))
    [x y]
    [ny-x ny-y]))

(defn endre-retning [spill [_ ny-retning]]
  (endre-i spill [:slange :retning]
           (partial bytt-retning-på-slangen ny-retning)))

(defn oppdater-spill [spill _]
  (if (:er-spillet-igang? spill)
    (neste-steg spill)
    spill))

(defn start-spill [spill _]
  (if-not (:er-spillet-igang? spill)
     (merge spill nytt-spill)
     spill))
