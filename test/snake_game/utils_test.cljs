(ns snake-game.utils-test
    (:require [snake-game.utils :as utils]
     [cljs.test :refer-macros [deftest is testing run-tests]]
     [cljs.spec :as s]
     [snake-game.spec :as spec]))

(s/instrument-ns 'snake-game.utils)

(deftest test-endre-retning
  (is (utils/endre-retning utils/nytt-spill [:endre-retning [0 1]])))

(s/instrument utils/oppdater-spill)
(deftest test-oppdater-spill
  (is (utils/oppdater-spill utils/nytt-spill :foo)))
