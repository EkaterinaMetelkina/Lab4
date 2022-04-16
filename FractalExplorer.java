import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

public class FractalExplorer{
    // Целочисленный размер отображения - это ширина и высота отображения в пикселях
    private int ScreenSize;
    // Ссылка JImageDisplay для обновления отображения с помощью различных методов как фрактал вычислен
    private JImageDisplay image;
    // Объект FractalGenerator для каждого типа фрактала
    private FractalGenerator fractal;
    // Определение диапазона, что мы в настоящее время показываем
    private Rectangle2D.Double range;

    // Конструктор, который принимает размер отображения, сохраняет его и инициализирует объекты диапазона и фрактал-генератора
    public FractalExplorer(int Size){
        this.ScreenSize = Size;
        fractal = new Mandelbrot(); // Инициализация фрактала Мандельброта
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        image = new JImageDisplay(Size, Size, 1);
    }

    // Этот метод инициализирует графический интерфейс Swing с помощью JFrame, содержащего Объект JImageDisplay и кнопку для очистки дисплея
    public void createAndShowGUI(){
        image.setLayout(new BorderLayout());
        JFrame frame = new JFrame("Fractal Explorer");
        frame.add(this.image, BorderLayout.CENTER);
        JButton reset = new JButton("Reset");
        frame.add(reset, BorderLayout.SOUTH);

        ButtonHandler resetHandler = new ButtonHandler();
        reset.addActionListener(resetHandler);
        MouseHandler click = new MouseHandler();
        image.addMouseListener(click);

        JPanel myPanel = new JPanel();
        frame.add(myPanel, BorderLayout.NORTH);
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(reset);
        frame.add(myBottomPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack ();
        frame.setVisible (true);
        frame.setResizable (false);
    }

    /**
     * Приватный вспомогательный метод для отображения фрактала. Этот метод проходит
     * через каждый пиксель на дисплее и вычисляет количество
     * итераций для соответствующих координат во фрактале
     * Область отображения. Если количество итераций равно -1, установит цвет пикселя.
     * в черный. В противном случае выберет значение в зависимости от количества итераций.
     * Обновит дисплей цветом для каждого пикселя и перекрасит
     * JImageDisplay, когда все пиксели нарисованы.
     */
    private void drawFractal(){
        for(int x = 0; x < ScreenSize; x++){
            for(int y = 0; y < ScreenSize; y++){
                double xCoord = FractalGenerator.getCoord (range.x, range.x + range.width, ScreenSize, x);
                double yCoord =  FractalGenerator.getCoord (range.y, range.y + range.height, ScreenSize, y);
                int num = fractal.numIterations(xCoord, yCoord);
                if (num == -1) {
                    this.image.drawPixel(x, y, 0);
                } else {
                    float hue = 0.7f + (float) num / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    this.image.drawPixel(x, y, rgbColor);
                }
            }
        }
        image.repaint();
    }

    // Внутренний класс для обработки событий ActionListener
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();

            if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }
        }
    }
    // Внутренний класс для обработки событий MouseAdapter
    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX();
            double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, ScreenSize, x);
            int y = e.getY();
            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, ScreenSize, y);

            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            drawFractal();
        }
    }

    // Точка входа
    public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(800);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}
