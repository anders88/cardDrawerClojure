(ns cardDrawerClojure.core-test
  (:use clojure.test
        cardDrawerClojure.core))

(deftest register-test
  (testing "That one can register and start a new game"
    (is (= {:players {"Darth" []}} (register-player {:players {}} "Darth")) "First player")
    (is (= {:players {"Darth" [] "Luke" []}} (register-player {:players {"Darth" []}} "Luke")) "Second player")
))

(deftest draw-card-test
  (testing "Drawing cards"
    (is (= {:players {"Darth" [1]} :deck []} (draw-card {:players {"Darth" []} :deck [1]} "Darth")) "Drawing the last card")
    )
  )

(deftest remove-from-all-test
  (testing "Remove all"
    (is (= {:a [1 2 3]} (remove-from-all {:a [1 2 4 3]} 4)))
    )
  )