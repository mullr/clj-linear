(ns clj-linear.core-test
  (:require [clojure.test :refer :all]
            [clj-linear.core :refer :all]))

(deftest expression-test
  (is (= {nil 1} (expression 1)))
  (is (= {:x 42} (expression (* :x 42))))
  (is (= {:x 42, :y 1, nil 7} (expression (+ (* :x 42) :y 7)))))

(deftest constraint-test
  (is (= {:op :=, :lhs {:x 2, :y 1}, :rhs {nil 42}}
         (constraint (= (+ (* 2 :x) :y -42) 0)))))

(deftest minimize-test
  (is (= {:x 4.0, :y 0.0}
         (minimize (expression (+ (* -2 :x) :y -5))
                   (constraints (<= (+ :x (* 2 :y)) 6)
                                (<= (+ (* 3 :x) (* 2 :y)) 12)
                                (>= :y 0))))))
