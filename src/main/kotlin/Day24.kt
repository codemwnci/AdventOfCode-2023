import java.io.File
import kotlin.time.*
import com.microsoft.z3.*


fun main() {
    Day24().puzzle1()
    Day24().puzzle2()
    Day24().puzzle2v2()
}

@OptIn(ExperimentalTime::class)
class Day24 {
    private val file = File("inputs/day24.txt")
    data class Point3D(val x:Double, val y:Double, val z:Double)
    data class Velocity3D(val x:Double, val y:Double, val z:Double)
    data class Hail(val p: Point3D, val v: Velocity3D) {
        fun slope() = v.y / v.x
    }

    fun puzzle1() {
        val hail = file.readLines().map {
            it.split(" @ ").let {
                Hail(it[0].split(", ").map { it.trim().toDouble() }.let { (x,y,z) -> Point3D(x,y,z) },
                     it[1].split(", ").map { it.trim().toDouble() }.let { (x,y,z) -> Velocity3D(x,y,z) })
            }
        }

        hail.combinations(2).count { calc2DOverlap(it[0], it[1]) }.printAnswer()
    }

    private fun calc2DOverlap(h1:Hail, h2:Hail): Boolean {
        fun validFuture(h:Hail, cx:Double, cy:Double): Boolean {
            return !((h.v.x < 0 && h.p.x < cx) || (h.v.x > 0 && h.p.x > cx) || (h.v.y < 0 && h.p.y < cy) || (h.v.y > 0 && h.p.y > cy))
        }

        if (h1.slope() == h2.slope()) return false
        val cx = ((h2.slope() * h2.p.x) - (h1.slope() * h1.p.x) + h1.p.y - h2.p.y) / (h2.slope() - h1.slope())
        val cy = (h1.slope() * (cx - h1.p.x)) + h1.p.y
        val valid = validFuture(h1, cx, cy) && validFuture(h2, cx, cy)

        return cx in 200000000000000.0..400000000000000.0 && cy in 200000000000000.0..400000000000000.0 && valid
    }


    // Initially completed this using Python, because I hadn't used Z3 before, and Reddit was full of python
    // people suggesting Z3 was the only way to solve the problem.
    // After solving, I wanted to find a Kotlin (JVM) way to solve the problem, and found a Java binding for Z3.
    // It was fiddly to set up (and the Java binding isn't as nice as the python approach), but for reference
    // Maven dependency I used was: io.github.p-org.solvers:z3:4.8.14-v5
    // Also need to download Z3 from https://github.com/Z3Prover/z3/releases/download/z3-4.12.4/z3-4.12.4-x64-win.zip
    // and make sure the bin directory is on the classpath -Djava.library.path="pathto\lib\z3-4.12.4-x64-win\bin\"
    fun puzzle2() {
        val hail = file.readLines().map {
            it.split(" @ ").let {
                Hail(it[0].split(", ").map { it.trim().toDouble() }.let { (x,y,z) -> Point3D(x,y,z) },
                    it[1].split(", ").map { it.trim().toDouble() }.let { (x,y,z) -> Velocity3D(x,y,z) })
            }
        }

        val ctx = Context() // if using proof = true, takes a significantly longer time
        val solver = ctx.mkSolver()

        val time = measureTime {
            // int version - Int version Took (without Proof = 4s / with Proof = 14m2s)
            val (x, y, z, vx, vy, vz) = listOf("x","y","z","vx","vy","vz").map { ctx.mkIntConst(it) }

            //hail.forEachIndexed { idx, h ->
            (0..2).forEach { idx ->                 // possible to get answers from 3 points, rather than calculating for all
                val h = hail[idx]
                val t = ctx.mkIntConst("t$idx")
                solver.add(ctx.mkEq(ctx.mkAdd(x, ctx.mkMul(vx, t)), ctx.mkAdd(ctx.mkInt(h.p.x.toLong()), ctx.mkMul(ctx.mkInt(h.v.x.toLong()), t))))
                solver.add(ctx.mkEq(ctx.mkAdd(y, ctx.mkMul(vy, t)), ctx.mkAdd(ctx.mkInt(h.p.y.toLong()), ctx.mkMul(ctx.mkInt(h.v.y.toLong()), t))))
                solver.add(ctx.mkEq(ctx.mkAdd(z, ctx.mkMul(vz, t)), ctx.mkAdd(ctx.mkInt(h.p.z.toLong()), ctx.mkMul(ctx.mkInt(h.v.z.toLong()), t))))
            }

            //println(solver)                            // if we want to print out the z3 grammar to check our values
            if (solver.check() == Status.SATISFIABLE) {  // check generates the model and confirms if it is satisfiable or not
                solver.model.eval(ctx.mkAdd(x, ctx.mkAdd(y, z)), false).printAnswer()
            }
        }

        println("Took $time to process")
    }

    fun puzzle2v2() {
        val hail = file.readLines().map {
            it.split(" @ ").let {
                Hail(it[0].split(", ").map { it.trim().toDouble() }.let { (x,y,z) -> Point3D(x,y,z) },
                    it[1].split(", ").map { it.trim().toDouble() }.let { (x,y,z) -> Velocity3D(x,y,z) })
            }
        }
        val ctx = Context() // if using proof = true, Real errors
        val solver = ctx.mkSolver()

        val time = measureTime {
            val (x, y, z, vx, vy, vz) = listOf("x","y","z","vx","vy","vz").map { ctx.mkRealConst(it) }
            (0..2).forEach {idx ->
                val h = hail[idx]
                val t = ctx.mkRealConst("t$idx")
                solver.add(ctx.mkEq(ctx.mkAdd(x, ctx.mkMul(vx, t)), ctx.mkAdd(ctx.mkReal(h.p.x.toLong()), ctx.mkMul(ctx.mkReal(h.v.x.toLong()), t))))
                solver.add(ctx.mkEq(ctx.mkAdd(y, ctx.mkMul(vy, t)), ctx.mkAdd(ctx.mkReal(h.p.y.toLong()), ctx.mkMul(ctx.mkReal(h.v.y.toLong()), t))))
                solver.add(ctx.mkEq(ctx.mkAdd(z, ctx.mkMul(vz, t)), ctx.mkAdd(ctx.mkReal(h.p.z.toLong()), ctx.mkMul(ctx.mkReal(h.v.z.toLong()), t))))
            }
            if (solver.check() == Status.SATISFIABLE) {
                solver.model.eval(ctx.mkAdd(x, ctx.mkAdd(y, z)), false).printAnswer()
            }
        }

        println("Took $time to process")
    }
}