package beam.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import beam.agentsim.infrastructure.QuadTreeBounds
import org.matsim.api.core.v01.Coord

import scala.util.Random


case class PointToPlot(val coord:Coord, val color:Color, val size:Int)


case class Bounds(minx: Double, miny: Double, maxx: Double, maxy: Double)

  class  BoundsCalculator(){
    var minX: Double = Double.MaxValue
    var maxX: Double = Double.MinValue
    var minY: Double = Double.MaxValue
    var maxY: Double = Double.MinValue


    def addPoint(coord:Coord)={
      minX = Math.min(minX, coord.getX)
      minY = Math.min(minY, coord.getY)
      maxX = Math.max(maxX, coord.getX)
      maxY = Math.max(maxY, coord.getY)
    }

    def getBound:Bounds={
      Bounds(minX,minY,maxX,maxY)
    }

    def getImageProjectedCoordinates(originalCoord:Coord, width:Int, height:Int):Coord={
      if (minX==maxX){
        new Coord(width/2, height/2)
      } else {
        new Coord((originalCoord.getX - minX) / (maxX - minX) * width, (originalCoord.getY - minY) / (maxY - minY) * height)
      }
    }
  }


class SpatialPlot(width:Int, height:Int){

  val pointsToPlot= collection.mutable.ListBuffer[PointToPlot]()

  val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);




  def addPoint(point: PointToPlot) = {
    pointsToPlot+=point
  }

  def writeImage(path: String): Unit ={
    val boundsCalculator=new BoundsCalculator
    for (pointToPlot <- pointsToPlot){
      boundsCalculator.addPoint(pointToPlot.coord)
    }
    val bound=boundsCalculator.getBound

    val graphics2d = bufferedImage.createGraphics();

    for (pointToPlot <- pointsToPlot){
      graphics2d.setColor(pointToPlot.color)
      val projectedCoord=boundsCalculator.getImageProjectedCoordinates(pointToPlot.coord,width,height)
      graphics2d.fillOval(projectedCoord.getX.toInt, projectedCoord.getY.toInt, pointToPlot.size,pointToPlot.size)
    }

    ImageIO.write(bufferedImage, "PNG", new File(path));
  }

}



object SpatialPlot extends App {
  /*
    val bi = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

    val ig2 = bi.createGraphics();
    //Draw some lines to the graphic


    //ig2.fillOval("sfdsfa", Random.nextFloat() * 1000, Random.nextFloat() * 1000)

    ig2.setColor(Color.BLACK)
    for (i <- 1 until 10) {
      ig2.fillOval(Random.nextInt(1000), Random.nextInt(1000), 5, 5)
    }

    ig2.setColor(Color.BLUE)
    for (i <- 1 until 10) {
      ig2.fillOval(Random.nextInt(1000), Random.nextInt(1000), 5, 5)
    }


    //ig2.drawLine(x1,y1,x2,y2);
    //ig2.drawLine(x2,y2,x3,y3);
    //...

    //Export the result to a file
    ImageIO.write(bi, "PNG", new File("c:\\temp\\name.png"));
    */

  val spatialPlot=new SpatialPlot(1000,1000)

  for (i <- 1 until 10) {
    spatialPlot.addPoint(PointToPlot(new Coord(Random.nextDouble(), Random.nextDouble()),Color.WHITE,5))
  }

  spatialPlot.writeImage("c:\\temp\\name.png")


}
