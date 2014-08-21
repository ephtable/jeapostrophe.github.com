#lang plai
(print-only-errors #t)

(define-type AE
  [num (n number?)]
  [binop (op procedure?)
         (lhs AE?)
         (rhs AE?)]
  [id (sym symbol?)]
  [app (fun AE?)
       (arg AE?)]
  [fun (param symbol?)
       (body AE?)]
  ;; [rec (name symbol?)
  ;;      (named-thing AE?)
  ;;      (body AE?)]
  [if0 (cond-e AE?)
       (true-e AE?)
       (false-e AE?)])

(define (with name named-thing body)
  (app (fun name body) named-thing))

(define (rec name named-thing body)  
  (define the-real-fac
    ;; (gensym)
    'the-real-fac)
  (define the-inner-fac
    ;; (gensym)
    'the-inner-fac)
  (with the-real-fac 
        (fun the-inner-fac
             (with name 
                   (fun 'n
                        (app (app (id the-inner-fac)
                                  (id the-inner-fac))
                             (id 'n)))
                   named-thing))
        (with name
              (app (id the-real-fac)
                   (id the-real-fac))
              body)))

;; <AE> := <real Racket number>
;;       | (+ <AE> <AE>)
;;       | (- <AE> <AE>)
;;       | (* <AE> <AE>)
;;       | (/ <AE> <AE>)
;;       | <id>
;;       | (with [<id> <AE>] <AE>)
;;       | (rec [<id> <AE>] <AE>)
;;       | (<AE> <AE>)
;;       | (fun (<id>) <AE>)
;;       | (if0 <AE> <AE> <AE>)

;; where <id> is any Racket symbol, except +, -, *, /, fun, if0, and with and rec

(define sym->binop
  (hasheq '+ +
          '- -
          '* *
          '/ /))

;; parse :: s-expression -> AE
(define (parse se)
  (cond
    [(and (list? se)
          (= 2 (length se)))
     (app (parse (first se))
          (parse (second se)))]
    [(and (list? se)
          (= 3 (length se))
          (equal? 'fun (first se))
          (list? (second se))
          (= 1 (length (second se)))
          (symbol? (first (second se))))
     (fun (first (second se))
          (parse (third se)))]
    [(and (list? se)
          (= 3 (length se))
          (equal? 'with (first se))
          (list? (second se))
          (= 2 (length (second se)))
          (symbol? (first (second se))))
     (with (first (second se))
           (parse (second (second se)))
           (parse (third se)))]
    [(and (list? se)
          (= 3 (length se))
          (equal? 'rec (first se))
          (list? (second se))
          (= 2 (length (second se)))
          (symbol? (first (second se))))
     (rec (first (second se))
          (parse (second (second se)))
          (parse (third se)))]
    [(symbol? se)
     (id se)]
    [(number? se)
     (num se)]
    [(and (list? se)
          (= 3 (length se))
          (hash-has-key? sym->binop (first se)))
     (binop (hash-ref sym->binop (first se))
            (parse (second se))
            (parse (third se)))]
    [(and (list? se)
          (= 4 (length se))
          (equal? 'if0 (first se)))
     (if0 (parse (second se))
          (parse (third se))
          (parse (fourth se)))]
    [else
     (error 'parse "Invalid syntax, dude: ~e" se)]))

(test (parse '1)
      (num 1))
(test (parse '(+ 1 1))
      (binop + (num 1) (num 1)))
(test (parse '(- 1 1))
      (binop - (num 1) (num 1)))
(test (parse 'x)
      (id 'x))
(test (parse '(with [x 27] x))
      (with 'x (num 27) (id 'x)))
(test (parse '(rec [x 27] x))
      (rec 'x (num 27) (id 'x)))
(test (parse '(double 5))
      (app (id 'double) (num 5)))
(test (parse '(fun (x) (+ x x)))
      (fun 'x (binop + (id 'x) (id 'x))))
(test (parse '(if0 0 1 2))
      (if0 (num 0) (num 1) (num 2)))

(test/exn (parse "1")
          "Invalid syntax")

(define-type Env
  [mtEnv]
  [consEnv
   (name symbol?)
   (named-value AEV?)
   (rest Env?)])

(define-type AEV
  [numV
   (n number?)]
  [closureV
   (param symbol?)
   (body AE?)
   (env Env?)])

(define (lookup-id $ sym)
  (type-case
   Env $
   [mtEnv 
    ()
    (error 'calc "You has a bad identifier, bro: ~e" sym)]
   [consEnv
    (name named-value rest)
    (if (symbol=? name sym)
      named-value
      (lookup-id rest sym))]))

;; calc : AE? Env? -> AEV?
;; compute the meaning of the AE
(define (calc ae $)
  (type-case
   AE ae
   ;; [rec
   ;;  (name named-thing body)
   ;;  (local
   ;;   [(define the-wrong-env
   ;;      $)
   ;;    (define named-value
   ;;      (calc named-thing ;; === (fun (n) ... fac ...)
   ;;            the-wrong-env))
   ;;    (define the-right-env
   ;;      (consEnv
   ;;       name ;; === 'fac
   ;;       named-value
   ;;       $))]

   ;;   ;; <1> = (consEnv 'fac (closureV 'n (.... fac ...) <1>) $)

   ;;    ;; named-value = (closureV 'n (... fac ...) the-wrong-env)
   ;;   (set-closureV-env! named-value
   ;;                      the-right-env)
   ;;    ;; named-value = (closureV 'n (... fac ...) the-right-env)
   ;;   (calc body
   ;;         the-right-env))]
   [fun
    (param body)
    (closureV param body $)]
   [app
    (fun arg)
    (type-case
     AEV (calc fun $)
     [closureV
      (arg-name fun-body fun-$)
      (calc fun-body 
            (consEnv
             arg-name
             (calc arg $)
             fun-$))]
     [else
      (error 'calc "Not a function, man")])]
   [id
    (sym)
    (lookup-id $ sym)]   
   [num
    (n)
    (numV n)]
   [if0
    (cond-e true-e false-e)
    (if (lift-numV zero? (calc cond-e $))
      (calc true-e $)
      (calc false-e $))]
   [binop
    (op lhs rhs)
    (numV
     (lift-numV
      op
      (calc lhs $)
      (calc rhs $)))]))

(define (lift-numV f . args)
  (apply f (map numV-n* args)))

(define (numV-n* a)
  (if (numV? a)
    (numV-n a)
    (error 'calc "Not a number: ~e" a)))

;; calc* : sexpr -> number?
(define (calc* se)
  (define res (calc (parse se) (mtEnv)))
  (type-case 
   AEV res
   [numV (n) n]
   [else res]))

(test (calc* '1)
      1)
(test (calc* '(+ 1 1))
      2)
(test (calc* '(- 0 1))
      -1)
(test (calc* '(* 2 1))
      2)
(test (calc* '(/ 4 2))
      2)

(test (calc* '(with [x (+ 5 5)]
                    (+ x x)))
      20)
(test (calc* '(with [y 7]
                    (with [x y]
                          (+ x x))))
      14)
(test (calc* '(with 
               [x (+ 5 5)]
               (with [x 7]
                     (+ x x))))
      14)
(test (calc* '(with [x (+ 5 5)]
                    (+ x (with [x 7]
                               (+ x x)))))
      24)
(test (calc* '(with [x (+ 5 5)]
                    (+ (with [x 7]
                               (+ x x))
                       x)))
      24)
(test (calc* '(with
               [x (+ 5 5)]
               (+ (with [x 7]
                        (+ x x))
                  (with [x 8]
                        (+ x x)))))
      (+ 14 16))
(test (calc* '(with [x 7]
                    (with [y (+ 2 x)]
                          (+ y 3))))
      12)
(test (calc* '(with [y 7]
                    (with [y (+ y 2)]
                          (+ y 3))))
      12)
(test (calc* '(with [x (+ 5 5)]
                    7))
      7)

(test (calc* '(with [x (+ 5 5)]
                    (+ x x)))
      20)
(test (calc* '(with [x (+ 5 6)]
                    (+ x x)))
      22)

(test (calc* '(with [x 5]
                     (with [y 5]
                           (+ (+ x x) y))))
      15)

(test (calc* '(with [x 5]
                     (with [x (+ 1 x)]
                           (+ (+ x x) x))))
      18)

(test/exn (calc* '(with [x 5]
                     (with [x (+ 1 x)]
                           (+ (+ x x) y))))
          "bro")

(test/exn (calc* '(with [x x]
                        5))
          "bro")


(test (calc* '(fun (x) (+ x x)))
      (closureV 'x (parse '(+ x x)) (mtEnv)))

(test (calc* '(+ 1 (+ 2 3)))
      6)

(test/exn (calc* '(+ 1 (fun (x) x)))
          "Not a number")

(test/exn (calc* '(5 1))
          "Not a function")

(test (calc* '(with [double (fun (x) (+ x x))]
                    (double 5)))
      10)

(test (calc* '(with [double (fun (x) (+ x x))]
                    (with [triple (fun (x) (+ x (double x)))]
                          (triple 5))))
      15)


(test (calc* '(with [double (fun (x) (+ x x))]
                    (with [triple (fun (x) (+ x (double x)))]
                          (with [double (fun (x) (- x 6))]
                                (triple 5)))))
      15)

(test (calc* '(if0 0 1 2))
      1)
(test (calc* '(if0 -1 1 2))
      2)


;; (define (f x y)
;;   (+ (* 6 x) (/ y 7)))

;; (f 1 2)

;; (define (fp x*y)
;;   (+ (* 6 (car x*y)) (/ (cdr x*y) 7)))

;; (fp (cons 1 2))

;; ;; Currying:
;; (define (fpp x)
;;   (λ (y)
;;     (+ (* 6 x) (/ y 7))))

;; ((fpp 1) 2)

(test (calc* '(rec [fac
                    (fun (n)
                         (if0 n
                              1
                              (* n (fac (- n 1)))))]
                   (fac 0)))
      1)

(test (calc* '(rec [fac
                    (fun (n)
                         (if0 n
                              1
                              (* n (fac (- n 1)))))]
                   (fac 1)))
      1)

(test (calc* '(rec [fac
                    (fun (n)
                         (if0 n
                              1
                              (* n (fac (- n 1)))))]
                   (fac 2)))
      2)

(test (calc* '(rec [fac
                    (fun (n)
                         (if0 n
                              1
                              (* n (fac (- n 1)))))]
                   (fac 5)))
      120)

;;(test (calc* '(rec [fac
;;                    7]
;;                   fac))
;;      7)

(test (calc* '(rec [f
                    (fun (x)
                         (+ x 7))]
                   (f 10)))
      17)

;;(test (calc* '(rec [f
;;                    (+ f 7)]
;;                   f))
;;      17)


(test (calc* '(fun (x) (x x)))
      (closureV 'x (app (id 'x) (id 'x)) (mtEnv)))

(test (calc* '(with [id (fun (x) x)]
                    (with [o (fun (x) (x x))]
                          (o id))))
      (closureV 'x (id 'x) (mtEnv)))

;; o is normally called "ω"
;; (o o) is normally called "Ω"

;; (test (calc* '(with [o (fun (x) (x x))]
;;                    (o o)))
;;      42)

(test (calc* '(with [fac
                     (fun (fac)
                          (fun (n)
                               (if0 n
                                    1
                                    (* n ((fac fac) (- n 1))))))]
                    ((fac fac) 5)))
      120)

(test (calc* '(with [the-real-fac
                     (fun (the-inner-fac)
                          (fun (n)
                               (if0 n
                                    1
                                    (* n ((the-inner-fac the-inner-fac) (- n 1))))))]
                    ((the-real-fac the-real-fac) 5)))
      120)

;; The Y combinator
(test (calc* '(with [the-real-fac
                     (fun (the-inner-fac)
                          (with [fac
                                 ;; (the-inner-fac the-inner-fac)
                                 (fun (n) ((the-inner-fac the-inner-fac) n))]
                                (fun (n)
                                     (if0 n
                                          1
                                          (* n (fac (- n 1)))))))]
                    ((the-real-fac the-real-fac) 5)))
      120)
