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

(defn finn-tilfeldige-ledige-plasser-på-brettet [antall slange [x y] {}]
  (let [slangens-plasser (putt-inni #{} (:kropp slange))
        hindre (putt-inni #{} (:hindre {}))
        alle-plasser (alle-plasser-på-brettet x y)]
    (when-let [ledige-plasser (seq (alle-ledige-plasser slangens-plasser alle-plasser))]
      (velg-en-tilfeldig ledige-plasser))))

(def brett [12 8])

(def slange {:retning [1 0]
             :kropp [[0 6]]})

(def nytt-spill {:brett brett
                 :slange slange
                 :hindre #{[1 0] [2 0] [10 0] [11 0]
                           [6 1] [7 1]
                           [6 2]
                           [6 3] [11 3]
                           [3 5]
                           [4 6] [5 6]
                           [0 7] [4 7] [6 7]} #_(finn-tilfeldige-ledige-plasser-på-brettet 5 slange brett)
                 :poeng 0
                 :er-spillet-igang? true})

(defn er-det-en-kollisjon? [{:keys [brett hindre slange]} ]
  (let [[x y] brett
        {:keys [retning kropp]} slange
        kant-x #{x -1}
        kant-y #{y -1}
        neste-x (+ (første-i retning) (første-i-første kropp))
        neste-y (+ (andre-i retning) (andre-i (første-i kropp)))]
    (or (finnes-i? kant-x neste-x)
        (finnes-i? kant-y neste-y)
        (hindre [neste-x neste-y])
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


(defn bytt-retning-på-slangen [[ny-x ny-y] [x y]]
  (if (or (= x ny-x)
          (= y ny-y))
    [x y]
    [ny-x ny-y]))

(defn endre-retning [spill [_ ny-retning]]
  (endre-i spill [:slange :retning]
           (partial bytt-retning-på-slangen ny-retning)))

(defn til-høyre [retning]
  (condp = retning
    [1 0]  [0 1]
    [0 1]  [-1 0]
    [-1 0] [0 -1]
    [0 -1]  [1 0]))

(defn kanskje-endre-retning [{:keys [brett slange] :as spill}]
  (if (er-det-en-kollisjon? spill)
    (endre-retning spill [nil (til-høyre (:retning slange))])
    spill))

(defn neste-steg [{:keys [slange brett] :as spill}]
  (if (er-det-en-kollisjon? spill)
    (avslutt spill)
    (-> spill
        (flytt-slangen)
        (kanskje-endre-retning))))


(defn oppdater-spill [spill _]
  (if (:er-spillet-igang? spill)
    (neste-steg spill)
    spill))

(defn start-spill [spill _]
  (if-not (:er-spillet-igang? spill)
     (merge spill nytt-spill)
     spill))
