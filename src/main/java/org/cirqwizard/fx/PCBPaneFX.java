/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cirqwizard.fx;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.cirqwizard.gerber.appertures.CircularAperture;
import org.cirqwizard.gerber.appertures.OctagonalAperture;
import org.cirqwizard.gerber.appertures.OvalAperture;
import org.cirqwizard.gerber.appertures.RectangularAperture;
import org.cirqwizard.gerber.appertures.macro.*;
import org.cirqwizard.geom.Arc;
import org.cirqwizard.geom.Point;
import org.cirqwizard.gerber.*;
import org.cirqwizard.generation.toolpath.CircularToolpath;
import org.cirqwizard.generation.toolpath.DrillPoint;
import org.cirqwizard.generation.toolpath.LinearToolpath;
import org.cirqwizard.generation.toolpath.Toolpath;

import java.io.IOException;
import java.util.List;


public class PCBPaneFX extends javafx.scene.layout.Region
{
    private static final double DEFAULT_SCALE = 0.005;

    public static final Color BACKGROUND_COLOR = Color.web("#ddfbdd");
    public static final Color ENABLED_TOOLPATH_COLOR = Color.web("#191970");
    public static final Color PASTE_TOOLPATH_COLOR = Color.GOLD;
    public static final Color DISABLED_TOOLPATH_COLOR = Color.web("#dcdcdc");
    public static final Color SELECTED_TOOLPATH_COLOR = Color.CYAN;
    public static final Color TOP_TRACE_COLOR = Color.RED;
    public static final Color BOTTOM_TRACE_COLOR = Color.BLUE;
    public static final Color DRILL_POINT_COLOR = Color.BLACK;
    public static final Color CONTOUR_COLOR = Color.MAGENTA;
    public static final Color SOLDER_PAD_COLOR = Color.NAVY;
    public static final Color PCB_BORDER = Color.BLACK;

    private Property<Double> scaleProperty = new SimpleObjectProperty<>(DEFAULT_SCALE);

    private double boardWidth;
    private double boardHeight;

    private java.util.List<GerberPrimitive> gerberPrimitives;
    private Property<ObservableList<Toolpath>> toolpaths = new SimpleListProperty<>();

    private Canvas canvas;
    private Rectangle selectionRectangle;

    private Color gerberColor = TOP_TRACE_COLOR;
    private Color toolpathColor = ENABLED_TOOLPATH_COLOR;

    private boolean flipHorizontal = false;

    public PCBPaneFX()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PcbPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        scaleProperty.addListener((v, oldV, newV) ->  repaint());
        toolpaths.addListener((v, oldV, newV) -> repaint());
    }

    public Property<ObservableList<Toolpath>> toolpathsProperty()
    {
        return toolpaths;
    }

    public void setGerberPrimitives(List<GerberPrimitive> gerberPrimitives)
    {
        this.gerberPrimitives = gerberPrimitives;
        repaint();
    }

    public void setGerberColor(Color gerberColor)
    {
        this.gerberColor = gerberColor;
    }

    public void setToolpathColor(Color toolpathColor)
    {
        this.toolpathColor = toolpathColor;
    }

    public boolean isFlipHorizontal()
    {
        return flipHorizontal;
    }

    public void setFlipHorizontal(boolean flipHorizontal)
    {
        this.flipHorizontal = flipHorizontal;
        repaint();
    }

    public void repaint()
    {
        getChildren().remove(canvas);
        renderImage();
        getChildren().add(canvas);
    }

    public void repaint(List<? extends Toolpath> toolpaths)
    {
        GraphicsContext g = canvas.getGraphicsContext2D();
        toolpaths.forEach(t -> renderToolpath(g, t));
    }

    private void renderImage()
    {
        canvas = new Canvas(boardWidth * scaleProperty.getValue() + 1, boardHeight * scaleProperty.getValue() + 1);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(BACKGROUND_COLOR);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setStroke(PCB_BORDER);
        g.setLineWidth(1);
        g.strokeRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
        g.scale(scaleProperty.getValue() * (flipHorizontal ? -1 : 1), -scaleProperty.getValue());
        g.translate(flipHorizontal ? -boardWidth : 0, -boardHeight);
        if (gerberPrimitives != null)
            for (GerberPrimitive primitive : gerberPrimitives)
                renderPrimitive(g, primitive);
        if (toolpaths.getValue() != null)
            for (Toolpath toolpath : toolpaths.getValue())
                renderToolpath(g, toolpath);
    }

    private void renderPrimitive(GraphicsContext g, GerberPrimitive primitive)
    {
        if (!(primitive instanceof Region) && !primitive.getAperture().isVisible())
            return;

        Color color = primitive.getPolarity() == GerberPrimitive.Polarity.DARK ? gerberColor : BACKGROUND_COLOR;
        g.setStroke(color);
        g.setFill(color);
        primitive.render(g);
    }

    private void renderToolpath(GraphicsContext g, Toolpath toolpath)
    {
        Color color = toolpath.isEnabled() ? toolpathColor : DISABLED_TOOLPATH_COLOR;
        if (toolpath.isSelected())
            color = SELECTED_TOOLPATH_COLOR;
        g.setStroke(color);
        if (toolpath instanceof LinearToolpath)
        {
            LinearToolpath linearToolpath = (LinearToolpath) toolpath;
            g.setLineCap(StrokeLineCap.ROUND);
            g.setLineWidth(linearToolpath.getToolDiameter());
            g.strokeLine(linearToolpath.getCurve().getFrom().getX(), linearToolpath.getCurve().getFrom().getY(),
                    linearToolpath.getCurve().getTo().getX(), linearToolpath.getCurve().getTo().getY());
        }
        else if (toolpath instanceof CircularToolpath)
        {
            CircularToolpath circularToolpath = (CircularToolpath) toolpath;
            g.setLineCap(StrokeLineCap.ROUND);
            g.setLineWidth(circularToolpath.getToolDiameter());
            Arc arc = (Arc) circularToolpath.getCurve();
            g.strokeArc(arc.getCenter().getX() - arc.getRadius(),
                    arc.getCenter().getY() - arc.getRadius(),
                    arc.getRadius() * 2, arc.getRadius() * 2,
                    -Math.toDegrees(arc.getStart()), Math.toDegrees(arc.getAngle()) * (arc.isClockwise() ? 1 : -1), ArcType.OPEN);
        }
        else if (toolpath instanceof DrillPoint)
        {
            DrillPoint drillPoint = (DrillPoint) toolpath;
            g.setFill(color);
            g.fillOval(drillPoint.getPoint().getX() - drillPoint.getToolDiameter() / 2,
                    drillPoint.getPoint().getY() - drillPoint.getToolDiameter() / 2,
                    drillPoint.getToolDiameter(), drillPoint.getToolDiameter());
        }
    }

    public void setSelection(Point2D point, double width, double height)
    {
        if (selectionRectangle != null)
            getChildren().remove(selectionRectangle);
        selectionRectangle = new Rectangle();
        selectionRectangle.setStrokeWidth(0.5);
        selectionRectangle.getStyleClass().add("pcb-selection-rect");
        // It seems that in this case transforms get converted to int somewhere down the road. So can't use them here
        selectionRectangle.setX(point.getX() * scaleProperty().getValue());
        selectionRectangle.setY((-point.getY() - height + boardHeight) * scaleProperty().getValue());
        selectionRectangle.setWidth(width * scaleProperty().getValue());
        selectionRectangle.setHeight(height * scaleProperty().getValue());
        getChildren().add(selectionRectangle);
    }

    public void clearSelection()
    {
        if (selectionRectangle != null)
        {
            getChildren().remove(selectionRectangle);
            selectionRectangle = null;
        }
    }

    public Property<Double> scaleProperty()
    {
        return scaleProperty;
    }

    public void setBoardWidth(double boardWidth)
    {
        this.boardWidth = boardWidth;
    }

    public void setBoardHeight(double boardHeight)
    {
        this.boardHeight = boardHeight;
    }
}
