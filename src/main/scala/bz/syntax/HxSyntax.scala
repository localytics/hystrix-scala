package bz.syntax

import bz.HxControl._
import bz.HxInterface
import com.netflix.hystrix.HystrixCommandGroupKey
import scalaz.\/

object hx {

  implicit class HxLift[M[_], A](fn: () => A)(
                                implicit hxi: HxInterface[M]) {

    def liftHx(gk: HystrixCommandGroupKey): M[A] =
      hxi.mHx(gk)(fn)
  }

  implicit class GKLift[A](gk: HystrixCommandGroupKey){
    def run[M[_]](fn: () => A)(implicit hxi: HxInterface[M]): M[A] =
      hxi.mHx(gk)(fn)
  }

  implicit class DjLift(gk: HystrixCommandGroupKey){
    def runC[A](fn: () => HxResult[A]): HxResult[A] =
      controlInterface(gk)(fn)
  }
}
