package org.cirqwizard.layers;

import org.cirqwizard.geom.Point;

import java.util.List;

public class Layer
{
    private List<? extends LayerElement> elements;

    public Layer()
    {
    }

    public Layer(List<? extends LayerElement> elements)
    {
        this.elements = elements;
    }

    public List<? extends LayerElement> getElements()
    {
        return elements;
    }

    public void setElements(List<? extends LayerElement> elements)
    {
        this.elements = elements;
    }

    public Point getMinPoint()
    {
        int minX = elements.stream().mapToInt(p -> p.getMin().getX()).min().getAsInt();
        int minY = elements.stream().mapToInt(p -> p.getMin().getY()).min().getAsInt();
        return new Point(minX, minY);
    }

    public Point getMaxPoint()
    {
        int maxX = elements.stream().mapToInt(p -> p.getMax().getX()).max().getAsInt();
        int maxY = elements.stream().mapToInt(p -> p.getMax().getY()).max().getAsInt();
        return new Point(maxX, maxY);
    }

    public void move(Point p)
    {
        elements.stream().forEach(e -> e.move(p));
    }

    public void rotate(boolean clockwise)
    {
        elements.stream().forEach(e -> e.rotate(clockwise));
    }

}