import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

class Keyword {
	val keyword:String
	val type : String
}

class Point{
	val id:String
	val title:String
	val url:String
}

class Link{
	val keyword: String
	val point:String
	val type:String
}

object mapper {
	def mapKeyword(point:Point) = {
		point.links.foreach(keyword => keyword)
	}
}
		

object PointGraph {

	def main(args: Array[String]) {
		
		val sc = new SparkContext("spark://node1.sidooo.com:7077", "Point Graph")
		

		 
		val rddPoint : RDD[(String, Point)]= sc.sequenceFile[String, Point]("/sewing/poing")
		val rddLink : RDD(String, Link)] = sc.sequenceFile[String, Link]("/sewing/link")

		val nodePoints = rddPoint.flatmapValue( x => (x.id, (x.title, x.url))
		val nodeLinks = rddLink.flatmapValue( x => (x.keyword, x.type))
		val edges = rddLink.flatmapValue( x => Edge(x.keyword, x.point))
		nodePoints.union(nodeLinks)

		val graph = Graph(points, links
	
	}
}