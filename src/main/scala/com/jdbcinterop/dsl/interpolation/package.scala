
package com.jdbcinterop.dsl

import scala.reflect.runtime.universe._

import com.jdbcinterop.core.{QueryBuilder, MkStatement}

package object interpolation {
  implicit class SQLHelper(val sc: StringContext) extends AnyVal {
    // Special case: No arguments 
    def sql: MkStatement = {
      QueryBuilder.empty.addLiteral(sc.parts.mkString("")).mkStatement
    }

    def sql[A : TypeTag](a: A): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

      def sql[A : TypeTag, B : TypeTag](a: A, b: B): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag](a: A, b: B, c: C): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag](a: A, b: B, c: C, d: D): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag](a: A, b: B, c: C, d: D, e: E): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag, Q : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag, Q : TypeTag, R : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag, Q : TypeTag, R : TypeTag, S : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag, Q : TypeTag, R : TypeTag, S : TypeTag, T : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder.addVariable[T](t)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag, Q : TypeTag, R : TypeTag, S : TypeTag, T : TypeTag, U : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder.addVariable[T](t)
      builder.addLiteral(strings.next())
      builder.addVariable[U](u)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }

    def sql[A : TypeTag, B : TypeTag, C : TypeTag, D : TypeTag, E : TypeTag, F : TypeTag, G : TypeTag, H : TypeTag, I : TypeTag, J : TypeTag, K : TypeTag, L : TypeTag, M : TypeTag, N : TypeTag, O : TypeTag, P : TypeTag, Q : TypeTag, R : TypeTag, S : TypeTag, T : TypeTag, U : TypeTag, V : TypeTag](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V, foo: String): MkStatement = {
      val strings = sc.parts.iterator
      val builder = QueryBuilder.empty

      builder.addLiteral(strings.next())
      builder.addVariable[A](a)
      builder.addLiteral(strings.next())
      builder.addVariable[B](b)
      builder.addLiteral(strings.next())
      builder.addVariable[C](c)
      builder.addLiteral(strings.next())
      builder.addVariable[D](d)
      builder.addLiteral(strings.next())
      builder.addVariable[E](e)
      builder.addLiteral(strings.next())
      builder.addVariable[F](f)
      builder.addLiteral(strings.next())
      builder.addVariable[G](g)
      builder.addLiteral(strings.next())
      builder.addVariable[H](h)
      builder.addLiteral(strings.next())
      builder.addVariable[I](i)
      builder.addLiteral(strings.next())
      builder.addVariable[J](j)
      builder.addLiteral(strings.next())
      builder.addVariable[K](k)
      builder.addLiteral(strings.next())
      builder.addVariable[L](l)
      builder.addLiteral(strings.next())
      builder.addVariable[M](m)
      builder.addLiteral(strings.next())
      builder.addVariable[N](n)
      builder.addLiteral(strings.next())
      builder.addVariable[O](o)
      builder.addLiteral(strings.next())
      builder.addVariable[P](p)
      builder.addLiteral(strings.next())
      builder.addVariable[Q](q)
      builder.addLiteral(strings.next())
      builder.addVariable[R](r)
      builder.addLiteral(strings.next())
      builder.addVariable[S](s)
      builder.addLiteral(strings.next())
      builder.addVariable[T](t)
      builder.addLiteral(strings.next())
      builder.addVariable[U](u)
      builder.addLiteral(strings.next())
      builder.addVariable[V](v)
      if (strings.hasNext) builder.addLiteral(strings.next())
      builder.mkStatement
    }
  }
}