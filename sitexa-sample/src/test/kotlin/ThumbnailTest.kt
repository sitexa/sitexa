import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.geometry.Positions
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by open on 12/05/2017.
 */

fun main(vararg: Array<String>) {
    Thumbnails.of(File("uploads/upload-1492333829830--754444700.jpeg"))
            .crop(Positions.CENTER)
            .size(200,200)
            .watermark(Positions.BOTTOM_RIGHT, ImageIO.read( File("uploads/watermark.png")), 0.5f)
            .outputQuality(0.8)
            .toFile( File("uploads/image-with-watermark.jpg"))
}