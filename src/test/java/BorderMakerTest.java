import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.opencv.core.Mat;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class BorderMakerTest {
    public static final String IMAGE_PATH = "~/Lenna.png";
    private Mat image;

    @BeforeEach
    public void loadImage() {
        imread(IMAGE_PATH, IMREAD_COLOR);
        Assertions.assertFalse(image.empty());
    }


}
