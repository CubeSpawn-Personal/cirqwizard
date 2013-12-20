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

package org.cirqwizard.settings;

import org.cirqwizard.fx.PCBSize;
import org.cirqwizard.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class Settings
{
    public static final int RESOLUTION = 1000;

    private static class PropertyNames
    {
        private static final String MACHINE_Y_DIFF_NAME = "machine.y.diff";
        private static final String MACHINE_REFERENCE_PIN_X = "machine.reference.pin.x";
        private static final String MACHINE_REFERENCE_PIN_Y = "machine.reference.pin.y";
        private static final String MACHINE_SMALL_PCB_WIDTH = "machine.small.pcb.width";
        private static final String MACHINE_LARGE_PCB_WIDTH = "machine.large.pcb.width";
        private static final String MACHINE_FAR_AWAY_Y = "general.far.away.y";

        private static final String SERIAL_PORT_NAME = "general.serial.port";
        private static final String LOGGER_LOG_LEVEL = "general.log.level";

        private static final String TRACES_TOOL_DIAMETER = "defaults.traces.tool.diameter";
        private static final String TRACES_FEED_XY = "defaults.traces.feed.xy";
        private static final String TRACES_FEED_Z = "defaults.traces.feed.z";
        private static final String TRACES_SPEED = "defaults.traces.speed";
        private static final String TRACES_CLEARANCE = "defaults.traces.clearance";
        private static final String TRACES_SAFETY_HEIGHT = "defaults.traces.safety.height";
        private static final String TRACES_DEFAULT_Z_OFFSET = "defaults.traces.z.offset";
        private static final String TRACES_WORKING_HEIGHT = "defaults.traces.working.height";

        private static final String DRILLING_FEED = "defaults.drilling.feed";
        private static final String DRILLING_SPEED = "defaults.drilling.speed";
        private static final String DRILLING_CLEARANCE = "defaults.drilling.clearance";
        private static final String DRILLING_SAFETY_HEIGHT = "defaults.drilling.safety.height";
        private static final String DRILLING_Z_OFFSET = "defaults.drilling.z.offset";
        private static final String DRILLING_WORKING_HEIGHT = "defaults.drilling.working.height";

        private static final String CONTOUR_FEED_XY = "defaults.contour.feed.xy";
        private static final String CONTOUR_FEED_Z = "defaults.contour.feed.z";
        private static final String CONTOUR_SPEED = "defaults.contour.speed";
        private static final String CONTOUR_CLEARANCE = "defaults.contour.clearance";
        private static final String CONTOUR_SAFETY_HEIGHT = "defaults.contour.safety.height";
        private static final String CONTOUR_Z_OFFSET = "defaults.contour.z.offset";
        private static final String CONTOUR_WORKING_HEIGHT = "defaults.contour.working.height";

        private static final String DISPENSING_NEEDLE_DIAMETER = "defaults.dispensing.needle.diameter";
        private static final String DISPENSING_PREFEED_PAUSE = "defaults.dispensing.prefeed.pause";
        private static final String DISPENSING_POSTFEED_PAUSE = "defaults.dispensing.postfeed.pause";
        private static final String DISPENSING_FEED = "defaults.dispensing.feed";
        private static final String DISPENSING_CLEARANCE = "defaults.dispensing.clearance";
        private static final String DISPENSING_Z_OFFSET = "defaults.dispensing.z.offset";
        private static final String DISPENSING_WORKING_HEIGHT = "defaults.working.height";
        private static final String DISPENSING_BLEEDING_DURATION = "dispensing.bleeding.duration";

        private static final String PP_PICKUP_HEIGHT = "pp.pickup.height";
        private static final String PP_MOVE_HEIGHT = "pp.move.height";
        private static final String PP_ROTATION_FEED = "pp.rotation.feed";

        private static final String INTERFACE_RECENT_FILES = "interface.recent.files";
        private static final String INTERFACE_G54_X = "interface.g54.x";
        private static final String INTERFACE_G54_Y = "interface.g54.y";
        private static final String INTERFACE_PCB_SIZE = "interface.pcb.size";
        private static final String INTERFACE_SCRAP_PLACE_X = "interface.scrap.place.x";
        private static final String INTERFACE_SCRAP_PLACE_Y = "interface.scrap.place.y";
        private static final String INTERFACE_TEST_CUT_DIRECTION = "interface.test.cut.direction";
    }

    private static class DefaultValues
    {
        private static final int MACHINE_SMALL_PCB_WIDTH = 65 * RESOLUTION;
        private static final int MACHINE_LARGE_PCB_WIDTH = 90 * RESOLUTION;
        private static final int MACHINE_FAR_AWAY_Y = 220 * RESOLUTION;

        private static final String LOGGER_LOG_LEVEL = "INFO";

        private static final int TRACES_TOOL_DIAMETER = (int)(0.3 * RESOLUTION);
        private static final int TRACES_FEED_XY = 300 * RESOLUTION;
        private static final int TRACES_FEED_Z = 200 * RESOLUTION;
        private static final String TRACES_SPEED = "1390";
        private static final int TRACES_CLEARANCE = 5 * RESOLUTION;
        private static final int TRACES_SAFETY_HEIGHT = 2 * RESOLUTION;
        private static final int TRACES_WORKING_HEIGHT = (int)(-0.05 * RESOLUTION);

        private static final int DRILLING_FEED = 200 * RESOLUTION;
        private static final String DRILLING_SPEED = "1390";
        private static final int DRILLING_CLEARANCE = 5 * RESOLUTION;
        private static final int DRILLING_SAFETY_HEIGHT = 2 * RESOLUTION;
        private static final int DRILLING_WORKING_HEIGHT = (int)(-2.5 * RESOLUTION);

        private static final int CONTOUR_FEED_XY = 300 * RESOLUTION;
        private static final int CONTOUR_FEED_Z = 200 * RESOLUTION;
        private static final String CONTOUR_SPEED = "1390";
        private static final int CONTOUR_CLEARANCE = 5 * RESOLUTION;
        private static final int CONTOUR_SAFETY_HEIGHT = 2 * RESOLUTION;
        private static final int CONTOUR_WORKING_HEIGHT = -2 * RESOLUTION;

        private static final int DISPENSING_NEEDLE_DIAMETER = (int)(0.4 * RESOLUTION);
        private static final int DISPENSING_PREFEED_PAUSE = (int)(0.1 * RESOLUTION);
        private static final int DISPENSING_POSTFEED_PAUSE = (int)(0.2 * RESOLUTION);
        private static final int DISPENSING_FEED = 100 * RESOLUTION;
        private static final int DISPENSING_CLEARANCE = 5 * RESOLUTION;
        private static final int DISPENSING_WORKING_HEIGHT = 0;
        private static final int DISPENSING_BLEEDING_DURATION = (int)(0.5 * RESOLUTION);

        private static final int PP_PICKUP_HEIGHT = (int)(-14.2 * RESOLUTION);
        private static final int PP_MOVE_HEIGHT = (int)(0.8 * RESOLUTION);
        private static final int PP_ROTATION_FEED = 100 * RESOLUTION;
    }

    private Preferences preferences;

    public Settings(Preferences preferences)
    {
        this.preferences = preferences;
    }

    private void flush()
    {
        try
        {
            preferences.flush();
        }
        catch (BackingStoreException e)
        {
            LoggerFactory.logException("Could not save preferences", e);
        }
    }

    private void set(String key, String value)
    {
        if (value == null)
            preferences.remove(key);
        else
            preferences.put(key, value);
        flush();
    }

    public void set(String key, int value)
    {
        preferences.putInt(key, value);
        flush();
    }

    public Integer getMachineYDiff()
    {
        if (preferences.get(PropertyNames.MACHINE_Y_DIFF_NAME, null) == null)
            return null;
        return preferences.getInt(PropertyNames.MACHINE_Y_DIFF_NAME, 0);
    }

    public void setMachineYDiff(int yDiff)
    {
        set(PropertyNames.MACHINE_Y_DIFF_NAME, yDiff);
    }

    public Integer getMachineReferencePinX()
    {
        if (preferences.get(PropertyNames.MACHINE_REFERENCE_PIN_X, null) == null)
            return null;
        return preferences.getInt(PropertyNames.MACHINE_REFERENCE_PIN_X, 0);
    }

    public void setMachineReferencePinX(int x)
    {
        set(PropertyNames.MACHINE_REFERENCE_PIN_X, x);
    }

    public Integer getMachineReferencePinY()
    {
        if (preferences.get(PropertyNames.MACHINE_REFERENCE_PIN_Y, null) == null)
            return null;
        return preferences.getInt(PropertyNames.MACHINE_REFERENCE_PIN_Y, 0);
    }

    public void setMachineReferencePinY(int y)
    {
        set(PropertyNames.MACHINE_REFERENCE_PIN_Y, y);
    }

    public Integer getMachineSmallPCBWidth()
    {
        return preferences.getInt(PropertyNames.MACHINE_SMALL_PCB_WIDTH, DefaultValues.MACHINE_SMALL_PCB_WIDTH);
    }

    public void setMachineSmallPCBWidth(int width)
    {
        set(PropertyNames.MACHINE_SMALL_PCB_WIDTH, width);
    }

    public Integer getMachineLargePCBWidth()
    {
        return preferences.getInt(PropertyNames.MACHINE_LARGE_PCB_WIDTH, DefaultValues.MACHINE_LARGE_PCB_WIDTH);
    }

    public void setMachineLargePCBWidth(int width)
    {
        set(PropertyNames.MACHINE_LARGE_PCB_WIDTH, width);
    }

    public Integer getFarAwayY()
    {
        return preferences.getInt(PropertyNames.MACHINE_FAR_AWAY_Y, DefaultValues.MACHINE_FAR_AWAY_Y);
    }

    public void setFarAwayY(int y)
    {
        set(PropertyNames.MACHINE_FAR_AWAY_Y, y);
    }

    public String getLogLevel()
    {
        return preferences.get(PropertyNames.LOGGER_LOG_LEVEL, DefaultValues.LOGGER_LOG_LEVEL);
    }

    public void setLogLevel(String logLevel)
    {
        set(PropertyNames.LOGGER_LOG_LEVEL, logLevel);
    }

    public String getSerialPort()
    {
        return preferences.get(PropertyNames.SERIAL_PORT_NAME, "");
    }

    public void setSerialPort(String serialPort)
    {
        set(PropertyNames.SERIAL_PORT_NAME, serialPort);
    }



    public Integer getDefaultTraceToolDiameter()
    {
        return preferences.getInt(PropertyNames.TRACES_TOOL_DIAMETER, DefaultValues.TRACES_TOOL_DIAMETER);
    }

    public void setDefaultTraceToolDiameter(int diameter)
    {
        set(PropertyNames.TRACES_TOOL_DIAMETER, diameter);
    }

    public Integer getDefaultTracesFeedXY()
    {
        return preferences.getInt(PropertyNames.TRACES_FEED_XY, DefaultValues.TRACES_FEED_XY);
    }

    public void setDefaultTracesFeedXY(int feed)
    {
        set(PropertyNames.TRACES_FEED_XY, feed);
    }

    public Integer getDefaultTracesFeedZ()
    {
        return preferences.getInt(PropertyNames.TRACES_FEED_Z, DefaultValues.TRACES_FEED_Z);
    }

    public void setDefaultTracesFeedZ(int feed)
    {
        set(PropertyNames.TRACES_FEED_Z, feed);
    }

    public String getDefaultTracesSpeed()
    {
        return preferences.get(PropertyNames.TRACES_SPEED, DefaultValues.TRACES_SPEED);
    }

    public void setDefaultTracesSpeed(int speed)
    {
        set(PropertyNames.TRACES_SPEED, speed);
    }

    public Integer getDefaultTracesClearance()
    {
        return preferences.getInt(PropertyNames.TRACES_CLEARANCE, DefaultValues.TRACES_CLEARANCE);
    }

    public void setDefaultTracesClearance(int clearance)
    {
        set(PropertyNames.TRACES_CLEARANCE, clearance);
    }

    public Integer getDefaultTracesSafetyHeight()
    {
        return preferences.getInt(PropertyNames.TRACES_SAFETY_HEIGHT, DefaultValues.TRACES_SAFETY_HEIGHT);
    }

    public void setDefaultTracesSafetyHeight(int height)
    {
        set(PropertyNames.TRACES_SAFETY_HEIGHT, height);
    }

    public Integer getDefaultTracesZOffset()
    {
        if (preferences.get(PropertyNames.TRACES_DEFAULT_Z_OFFSET, null) == null)
            return null;
        return preferences.getInt(PropertyNames.TRACES_DEFAULT_Z_OFFSET, 0);
    }

    public void setDefaultTracesZOFfset(int offset)
    {
        set(PropertyNames.TRACES_DEFAULT_Z_OFFSET, offset);
    }

    public Integer getDefaultTracesWorkingHeight()
    {
        return preferences.getInt(PropertyNames.TRACES_WORKING_HEIGHT, DefaultValues.TRACES_WORKING_HEIGHT);
    }

    public void setDefaultTracesWorkingHeight(int height)
    {
        set(PropertyNames.TRACES_WORKING_HEIGHT, height);
    }



    public Integer getDefaultDrillingFeed()
    {
        return preferences.getInt(PropertyNames.DRILLING_FEED, DefaultValues.DRILLING_FEED);
    }

    public void setDefaultDrillingFeed(int feed)
    {
        set(PropertyNames.DRILLING_FEED, feed);
    }

    public String getDefaultDrillingSpeed()
    {
        return preferences.get(PropertyNames.DRILLING_SPEED, DefaultValues.DRILLING_SPEED);
    }

    public void setDefaultDrillingSpeed(String speed)
    {
        set(PropertyNames.DRILLING_SPEED, speed);
    }

    public Integer getDefaultDrillingClearance()
    {
        return preferences.getInt(PropertyNames.DRILLING_CLEARANCE, DefaultValues.DRILLING_CLEARANCE);
    }

    public void setDefaultDrillingClearance(int clearance)
    {
        set(PropertyNames.DRILLING_CLEARANCE, clearance);
    }

    public Integer getDefaultDrillingSafetyHeight()
    {
        return preferences.getInt(PropertyNames.DRILLING_SAFETY_HEIGHT, DefaultValues.DRILLING_SAFETY_HEIGHT);
    }

    public void setDefaultDrillingSafetyHeight(int height)
    {
        set(PropertyNames.DRILLING_SAFETY_HEIGHT, height);
    }

    public Integer getDefaultDrillingZOffset()
    {
        if (preferences.get(PropertyNames.DRILLING_Z_OFFSET, null) == null)
            return null;
        return preferences.getInt(PropertyNames.DRILLING_Z_OFFSET, 0);
    }

    public void setDefaultDrillingZOffset(int offset)
    {
        set(PropertyNames.DRILLING_Z_OFFSET, offset);
    }

    public Integer getDefaultDrillingWorkingHeight()
    {
        return preferences.getInt(PropertyNames.DRILLING_WORKING_HEIGHT, DefaultValues.DRILLING_WORKING_HEIGHT);
    }

    public void setDefaultDrillingWorkingHeight(int height)
    {
        set(PropertyNames.DRILLING_WORKING_HEIGHT, height);
    }



    public Integer getDefaultContourFeedXY()
    {
        return preferences.getInt(PropertyNames.CONTOUR_FEED_XY, DefaultValues.CONTOUR_FEED_XY);
    }

    public void setDefaultContourFeedXY(int feed)
    {
        set(PropertyNames.CONTOUR_FEED_XY, feed);
    }

    public Integer getDefaultContourFeedZ()
    {
        return preferences.getInt(PropertyNames.CONTOUR_FEED_Z, DefaultValues.CONTOUR_FEED_Z);
    }

    public void setDefaultContourFeedZ(int feed)
    {
        set(PropertyNames.CONTOUR_FEED_Z, feed);
    }

    public String getDefaultContourSpeed()
    {
        return preferences.get(PropertyNames.CONTOUR_SPEED, DefaultValues.CONTOUR_SPEED);
    }

    public void setDefaultContourSpeed(String speed)
    {
        set(PropertyNames.CONTOUR_SPEED, speed);
    }

    public Integer getDefaultContourClearance()
    {
        return preferences.getInt(PropertyNames.CONTOUR_CLEARANCE, DefaultValues.CONTOUR_CLEARANCE);
    }

    public void setDefaultContourClearance(int clearance)
    {
        set(PropertyNames.CONTOUR_CLEARANCE, clearance);
    }

    public Integer getDefaultContourSafetyHeight()
    {
        return preferences.getInt(PropertyNames.CONTOUR_SAFETY_HEIGHT, DefaultValues.CONTOUR_SAFETY_HEIGHT);
    }

    public void setDefaultContourSafetyHeight(int height)
    {
        set(PropertyNames.CONTOUR_SAFETY_HEIGHT, height);
    }

    public Integer getDefaultContourZOffset()
    {
        if (preferences.get(PropertyNames.CONTOUR_Z_OFFSET, null) == null)
            return null;
        return preferences.getInt(PropertyNames.CONTOUR_Z_OFFSET, 0);
    }

    public void setDefaultContourZOffset(int offset)
    {
        set(PropertyNames.CONTOUR_Z_OFFSET, offset);
    }

    public Integer getDefaultContourWorkingHeight()
    {
        return preferences.getInt(PropertyNames.CONTOUR_WORKING_HEIGHT, DefaultValues.CONTOUR_WORKING_HEIGHT);
    }

    public void setDefaultContourWorkingHeight(int height)
    {
        set(PropertyNames.CONTOUR_WORKING_HEIGHT, height);
    }


    public Integer getDefaultDispensingNeedleDiameter()
    {
        return preferences.getInt(PropertyNames.DISPENSING_NEEDLE_DIAMETER, DefaultValues.DISPENSING_NEEDLE_DIAMETER);
    }

    public void setDefaultDispensingNeedleDiameter(int diameter)
    {
        set(PropertyNames.DISPENSING_NEEDLE_DIAMETER, diameter);
    }

    public Integer getDefaultDispensingPrefeedPause()
    {
        return preferences.getInt(PropertyNames.DISPENSING_PREFEED_PAUSE, DefaultValues.DISPENSING_PREFEED_PAUSE);
    }

    public void setDefaultDispensingPrefeedPause(int pause)
    {
        set(PropertyNames.DISPENSING_PREFEED_PAUSE, pause);
    }

    public Integer getDefaultDispensingFeed()
    {
        return preferences.getInt(PropertyNames.DISPENSING_FEED, DefaultValues.DISPENSING_FEED);
    }

    public void setDefaultDispensingFeed(int feed)
    {
        set(PropertyNames.DISPENSING_FEED, feed);
    }

    public Integer getDefaultDispensingClearance()
    {
        return preferences.getInt(PropertyNames.DISPENSING_CLEARANCE, DefaultValues.DISPENSING_CLEARANCE);
    }

    public void setDefaultDispensingClearance(int clearance)
    {
        set(PropertyNames.DISPENSING_CLEARANCE, clearance);
    }

    public Integer getDefaultDispensingZOffset()
    {
        if (preferences.get(PropertyNames.DISPENSING_Z_OFFSET, "") == null)
            return null;
        return preferences.getInt(PropertyNames.DISPENSING_Z_OFFSET, 0);
    }

    public void setDefaultDispensingZOffset(int offset)
    {
        set(PropertyNames.DISPENSING_Z_OFFSET, offset);
    }

    public Integer getDefaultDispensingWorkingHeight()
    {
        return preferences.getInt(PropertyNames.DISPENSING_WORKING_HEIGHT, DefaultValues.DISPENSING_WORKING_HEIGHT);
    }

    public void setDefaultDispensingWorkingHeight(int height)
    {
        set(PropertyNames.DISPENSING_WORKING_HEIGHT, height);
    }

    public Integer getDispensingBleedingDuration()
    {
        return preferences.getInt(PropertyNames.DISPENSING_BLEEDING_DURATION, DefaultValues.DISPENSING_BLEEDING_DURATION);
    }

    public void setDispensingBleedingDuration(int duration)
    {
        set(PropertyNames.DISPENSING_BLEEDING_DURATION, duration);
    }

    public Integer getDispensingPostfeedPause()
    {
        return preferences.getInt(PropertyNames.DISPENSING_POSTFEED_PAUSE, DefaultValues.DISPENSING_POSTFEED_PAUSE);
    }

    public void setDispensingPostfeedPause(int duration)
    {
        set(PropertyNames.DISPENSING_POSTFEED_PAUSE, duration);
    }



    public Integer getPPPickupHeight()
    {
        return preferences.getInt(PropertyNames.PP_PICKUP_HEIGHT, DefaultValues.PP_PICKUP_HEIGHT);
    }

    public void setPPPickupHeight(int height)
    {
        set(PropertyNames.PP_PICKUP_HEIGHT, height);
    }

    public Integer getPPMoveHeight()
    {
        return preferences.getInt(PropertyNames.PP_MOVE_HEIGHT, DefaultValues.PP_MOVE_HEIGHT);
    }

    public void setPPMoveHeight(int height)
    {
        set(PropertyNames.PP_MOVE_HEIGHT, height);
    }

    public Integer getPPRotationFeed()
    {
        return preferences.getInt(PropertyNames.PP_ROTATION_FEED, DefaultValues.PP_ROTATION_FEED);
    }

    public void setPPRotationFeed(int feed)
    {
        set(PropertyNames.PP_ROTATION_FEED, feed);
    }



    public List<String> getRecentFiles()
    {
        ArrayList<String> files = new ArrayList<String>();
        for (int i = 1; i <= 5; i++)
        {
            String str = preferences.get(PropertyNames.INTERFACE_RECENT_FILES + "." + i, null);
            if (str == null)
                break;
            files.add(str);
        }
        return files;
    }

    public void setRecentFile(String file)
    {
        List<String> files = getRecentFiles();
        if (files.indexOf(file) >= 0)
            files.remove(file);
        files.add(0, file);
        for (int i = 0; i < Math.min(files.size(), 5); i++)
            set(PropertyNames.INTERFACE_RECENT_FILES + "." + (i + 1), files.get(i));
        flush();
    }

    public Integer getG54X()
    {
        if (preferences.get(PropertyNames.INTERFACE_G54_X, null) == null)
            return null;
        return preferences.getInt(PropertyNames.INTERFACE_G54_X, 0);
    }

    public void setG54X(int x)
    {
        set(PropertyNames.INTERFACE_G54_X, x);
    }

    public Integer getG54Y()
    {
        if (preferences.get(PropertyNames.INTERFACE_G54_Y, null) == null)
            return null;
        return preferences.getInt(PropertyNames.INTERFACE_G54_Y, 0);
    }

    public void setG54Y(int y)
    {
        set(PropertyNames.INTERFACE_G54_Y, y);
    }

    public PCBSize getPCBSize()
    {
        return PCBSize.valueOf(preferences.getInt(PropertyNames.INTERFACE_PCB_SIZE, -1));
    }

    public void setPCBSize(PCBSize pcbSize)
    {
        preferences.putInt(PropertyNames.INTERFACE_PCB_SIZE, pcbSize.getStoreValue());
        flush();
    }

    public Integer getScrapPlaceX()
    {
        if (preferences.get(PropertyNames.INTERFACE_SCRAP_PLACE_X, null) == null)
            return null;
        return preferences.getInt(PropertyNames.INTERFACE_SCRAP_PLACE_X, 0);
    }

    public void setScrapPlaceX(int x)
    {
        set(PropertyNames.INTERFACE_SCRAP_PLACE_X, x);
    }

    public Integer getScrapPlaceY()
    {
        if (preferences.get(PropertyNames.INTERFACE_SCRAP_PLACE_Y, null) == null)
            return null;
        return preferences.getInt(PropertyNames.INTERFACE_SCRAP_PLACE_Y, 0);
    }

    public void setScrapPlaceY(int y)
    {
        set(PropertyNames.INTERFACE_SCRAP_PLACE_Y, y);
    }

    public int getTestCutDirection()
    {
        return preferences.getInt(PropertyNames.INTERFACE_TEST_CUT_DIRECTION, -1);
    }

    public void setTestCutDirection(int direction)
    {
        preferences.putInt(PropertyNames.INTERFACE_TEST_CUT_DIRECTION, direction);
    }

}
