(ns clj-linear.builder
  (:require [clojure.test :refer :all]
            [clj-linear.builder :refer :all]
            [clj-linear.expression :as expr]))

(deftest term-test
  (is (= {nil 1} (term 1)))
  (is (= {:x 1} (term :x)))
  (is (= {:x 42} (term {:x 42})))
  (is (= {:x 42} (term :x 42)))
  (is (= {:x 42} (term 42 :x))))

(deftest expression-test
  (is (= {:x  42} (expression {:x 42})))
  (is (= {:x 1} (expression :x)))
  (is (= {nil 42} (expression 42))))

(deftest build-test
  (is (= {nil 4} (plus 2 2)))
  (is (= {nil 6} (multiply 2 3)))
  (is (= {:x 1, :y 2} (plus :x (multiply :y 2))))
  (is (= {:op :=, :lhs {:x 1, :y -1}, :rhs {nil 42}}
         (equals (plus :x (minus :y) -42) 0))))
