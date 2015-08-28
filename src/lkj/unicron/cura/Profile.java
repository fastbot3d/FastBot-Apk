package lkj.unicron.cura;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import lkj.unicron.util.Log;

public class Profile {
	public class ItemConfig {
		String name ;
		Object value;
		
		public boolean getBooleanValue(){
			return (Boolean) (value);
		}
		public float getFloatValue(){
			return (Float) value;
		}
		public String getStringValue(){
			return (String) value;
		}
		public Object getValue(){
			return value;
		}
		public String getName(){
			return name;
		}
		
/*		public String getKeyValue(){
			String k_v = null;
			if(){
				
			}
			return name + "=" + k_v;
		}*/
		
		public void setValue(Object v){
			value = v;
		}
		public void setName(String na){
			name = na;
		}
	}
	
	public HashMap <String, ItemConfig> configMap= new HashMap<String, ItemConfig>();
	//final  int totalItem=20;
	public static String ItemName[] = {"layer_height", "wall_thickness","retraction_enable", "solid_layer_thickness","fill_density", 
			"print_speed","support", "platform_adhesion","nozzle_size", "bottom_thickness","object_sink", "overlap_dual",
			"travel_speed", "bottom_layer_speed","infill_speed", "inset0_speed","insetx_speed", 
			"cool_min_layer_time","fan_enabled"};
	
	//"startCode", "endCode"
	public static String curaSettingItemName[] = { "layerThickness", "initialLayerThickness", "filamentDiameter", "filamentFlow", 
		"extrusionWidth", "insetCount", "downSkinCount", "upSkinCount", 
		"infillOverlap",  "initialSpeedupLayers", "initialLayerSpeed", 
		"printSpeed",    "infillSpeed", "inset0Speed", "insetXSpeed", "moveSpeed", 
		"fanSpeedMin", "fanSpeedMax", "supportAngle", "supportEverywhere", 
		"supportLineDistance", "supportXYDistance", "supportZDistance", 
		"supportExtruder", "retractionAmount", "retractionSpeed", "retractionMinimalDistance", 
		"retractionAmountExtruderSwitch", "retractionZHop", "minimalExtrusionBeforeRetraction",
		"enableCombing", "multiVolumeOverlap", "objectSink", "minimalLayerTime", "minimalFeedrate", 
		"coolHeadLift", "extruderOffset[1].X", "extruderOffset[1].Y", 
		"extruderOffset[2].X", "extruderOffset[2].Y", "extruderOffset[3].X", "extruderOffset[3].Y", 
		"fixHorrible", "fanFullOnLayerNr", "supportType", "sparseInfillLineDistance", "sparseInfillLineDistance", 
		"downSkinCount", "upSkinCount", "raftMargin", "raftLineSpacing", "raftBaseThickness", 
		"raftBaseLinewidth", "raftInterfaceThickness", "raftInterfaceLinewidth", "skirtDistance", 
		"skirtLineCount", "skirtMinLength", "fixHorrible", "layerThickness", "gcodeFlavor", "spiralizeMode", "wipeTowerSize", "enableOozeShield"};
	
 	public Profile(Context context) {
 		getProfile(context, "full_config");
	}
 	
 	public Profile(Context context, String preferenceName) {
 		getProfile(context, preferenceName);
	}
 	
 	public void getProfile(Context context, String preferenceName) {
		ItemConfig a1;
		
		SharedPreferences config = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
		for(String key : Profile.ItemName){
			String val = config.getString(key, "0");
			a1= new ItemConfig();
			a1.setValue(val);
			a1.setName(key);
			configMap.put(key, a1);
			Log.d("get_full_config key=" + key + ", val=" +  val);
		}
	}
	
 	public void setItem(String name, Object v){
 		ItemConfig a1 = configMap.get(name);
 		if(a1 != null){
 			a1.setValue(v);
 			configMap.put(name, a1);
 		} else {
 			Log.e("set methodnot exist name=" + name );
 		}
 		
 	}
 	
 	public Object getItem(String name){
 		Object obj = null;
 		ItemConfig a1 = configMap.get(name);
 		if(a1 != null){
 			obj = a1.getValue();
 		} else {
 			Log.e("get method,not exist name=" + name );
 		}
 		return obj;
 	}
 	
 	static public float calculateEdgeWidth(Context context){
 		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
 		float wall_thickness = Float.parseFloat(config.getString("wall_thickness", "0"));
 		float nozzle_size = Float.parseFloat(config.getString("nozzle_size", "0"));
 		int spiralize = Integer.parseInt(config.getString("spiralize", "0"));
		
 		if (spiralize == 1)
 			return wall_thickness;
 					
 		if (wall_thickness < 0.01)
 			return nozzle_size ;
 		
 		if (wall_thickness < nozzle_size)
 			return wall_thickness;

 		int lineCount = (int) (wall_thickness / (nozzle_size - 0.0001));
 		if (lineCount == 0)
 			return nozzle_size;
 		
 		float lineWidth = wall_thickness / lineCount;
 		float lineWidthAlt = wall_thickness / (lineCount + 1);
 		if (lineWidth > nozzle_size * 1.5)
 			return lineWidthAlt;
 		return lineWidth;
 	}
 	
 	static public float calculateLineCount(Context context){
 		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
 		float wallThickness = Float.parseFloat(config.getString("wall_thickness", "0"));
 		float nozzleSize = Float.parseFloat(config.getString("nozzle_size", "0"));
 		int spiralize = Integer.parseInt(config.getString("spiralize", "0"));

		if (wallThickness < 0.01)
			return 0;
		if (wallThickness < nozzleSize)
			return 1;
 		if (spiralize == 1)
 			return 1;

		int lineCount = (int)(wallThickness / (nozzleSize - 0.0001));
		if (lineCount < 1)
			lineCount = 1;
		float lineWidth = wallThickness / lineCount;
		float lineWidthAlt = wallThickness / (lineCount + 1);
		if (lineWidth > nozzleSize * 1.5)
			return lineCount + 1;
		return lineCount;
		
 	}
 	
 	static public float calculateSolidLayerCount(Context context){
 		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
 		float layerHeight = Float.parseFloat(config.getString("layer_height", "0"));
 		float solidThickness = Float.parseFloat(config.getString("solid_layer_thickness", "0"));
 		
		if (layerHeight == 0.0)
			return 1;
		return (int)(Math.ceil(solidThickness / (layerHeight - 0.0001)));
 	}
 	
 	static public float calculateObjectSizeOffsets(Context context){
 		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
 		String platform_adhesion = config.getString("platform_adhesion", "0");
 		float skirt_line_count = Float.parseFloat(config.getString("skirt_line_count", "0"));
 		float brim_line_count = Float.parseFloat(config.getString("brim_line_count", "0"));
 		float skirt_gap = Float.parseFloat(config.getString("skirt_gap", "0"));
 		
 		float size = 0.0f;

		if (platform_adhesion.equals("Brim"))
			size += brim_line_count * calculateEdgeWidth(context); 
		else if (platform_adhesion.equals("Raft"))
			;
		else {
			if (skirt_line_count > 0)
				size += skirt_line_count * calculateEdgeWidth(context) + skirt_gap;
		}
		return size;
 	}
 	
 	static public float[] getMachineCenterCoords(Context context){
 		SharedPreferences machine_config = context.getSharedPreferences("machine_config", Activity.MODE_PRIVATE);
 		float[] ret = new float[1];
 		Boolean machine_center_is_zero = Boolean.parseBoolean(machine_config.getString("machine_center_is_zero", "0"));
 		float machine_width = Float.parseFloat(machine_config.getString("machine_width", "0"));
 		float machine_depth = Float.parseFloat(machine_config.getString("machine_depth", "0"));
 		if (machine_center_is_zero){
 			ret[0]=0f;
 			ret[1]=0f;
 		}
 		
 		ret[0]=machine_width / 2;
		ret[1]=machine_depth / 2;
		return ret;
 	}
 	
 	static public int minimalExtruderCount(Context context){
 		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
 		SharedPreferences machine_config = context.getSharedPreferences("machine_config", Activity.MODE_PRIVATE);
 		int extruder_amount = Integer.parseInt(machine_config.getString("extruder_amount", "0"));
 		String support = config.getString("support", "0");
 		String support_dual_extrusion = config.getString("support_dual_extrusion", "0");
 		
 		if (extruder_amount < 2)
 			return 1;
 		if (support.equals("None"))
 			return 1;
 		if (support_dual_extrusion.equals("Second extruder"))
 			return 2;
 		return 1;
 	}
 	
 	static public String getGCodeExtension(Context context){
 		SharedPreferences machine_config = context.getSharedPreferences("machine_config", Activity.MODE_PRIVATE);
 		String gcode_flavor = machine_config.getString("gcode_flavor", "0");
 		
 		if (gcode_flavor.equals("BFB"))
 			return ".bfb";
 		return ".gcode";
 	}
 	
 	static public String getAlterationFileContents(Context context, String str){
 		SharedPreferences machine_config = context.getSharedPreferences("machine_config", Activity.MODE_PRIVATE);
 		String gcode_flavor = machine_config.getString("gcode_flavor", "0");
 		
 		if (gcode_flavor.equals("UltiGCode")){
 			if (str.equals("end.gcode"))
 				return "M25 ;Stop reading from this point on";   // CURA_PROFILE_STRING:%s\n' % (getProfileString())
 			return "";
 		}
 		return "";
 	}
 	
 	

/*
def calculateEdgeWidth():
	wallThickness = getProfileSettingFloat('wall_thickness')
	nozzleSize = getProfileSettingFloat('nozzle_size')

	if getProfileSetting('spiralize') == 'True':
		return wallThickness

	if wallThickness < 0.01:
		return nozzleSize
	if wallThickness < nozzleSize:
		return wallThickness

	lineCount = int(wallThickness / (nozzleSize - 0.0001))
	if lineCount == 0:
		return nozzleSize
	lineWidth = wallThickness / lineCount
	lineWidthAlt = wallThickness / (lineCount + 1)
	if lineWidth > nozzleSize * 1.5:
		return lineWidthAlt
	return lineWidth

def calculateLineCount():
	wallThickness = getProfileSettingFloat('wall_thickness')
	nozzleSize = getProfileSettingFloat('nozzle_size')

	if wallThickness < 0.01:
		return 0
	if wallThickness < nozzleSize:
		return 1
	if getProfileSetting('spiralize') == 'True':
		return 1

	lineCount = int(wallThickness / (nozzleSize - 0.0001))
	if lineCount < 1:
		lineCount = 1
	lineWidth = wallThickness / lineCount
	lineWidthAlt = wallThickness / (lineCount + 1)
	if lineWidth > nozzleSize * 1.5:
		return lineCount + 1
	return lineCount

def calculateSolidLayerCount():
	layerHeight = getProfileSettingFloat('layer_height')
	solidThickness = getProfileSettingFloat('solid_layer_thickness')
	if layerHeight == 0.0:
		return 1
	return int(math.ceil(solidThickness / (layerHeight - 0.0001)))

def calculateObjectSizeOffsets():
	size = 0.0

	if getProfileSetting('platform_adhesion') == 'Brim':
		size += getProfileSettingFloat('brim_line_count') * calculateEdgeWidth()
	elif getProfileSetting('platform_adhesion') == 'Raft':
		pass
	else:
		if getProfileSettingFloat('skirt_line_count') > 0:
			size += getProfileSettingFloat('skirt_line_count') * calculateEdgeWidth() + getProfileSettingFloat('skirt_gap')

	#if getProfileSetting('enable_raft') != 'False':
	#	size += profile.getProfileSettingFloat('raft_margin') * 2
	#if getProfileSetting('support') != 'None':
	#	extraSizeMin = extraSizeMin + numpy.array([3.0, 0, 0])
	#	extraSizeMax = extraSizeMax + numpy.array([3.0, 0, 0])
	return [size, size]

def getMachineCenterCoords():
	if getMachineSetting('machine_center_is_zero') == 'True':
		return [0, 0]
	return [getMachineSettingFloat('machine_width') / 2, getMachineSettingFloat('machine_depth') / 2]

def minimalExtruderCount():
	if int(getMachineSetting('extruder_amount')) < 2:
		return 1
	if getProfileSetting('support') == 'None':
		return 1
	if getProfileSetting('support_dual_extrusion') == 'Second extruder':
		return 2
	return 1

def getGCodeExtension():
	if getMachineSetting('gcode_flavor') == 'BFB':
		return '.bfb'
	return '.gcode'



def getAlterationFileContents(filename, extruderCount = 1):
	prefix = ''
	postfix = ''
	alterationContents = getAlterationFile(filename)
	if getMachineSetting('gcode_flavor') == 'UltiGCode':
		if filename == 'end.gcode':
			return 'M25 ;Stop reading from this point on.\n;CURA_PROFILE_STRING:%s\n' % (getProfileString())
		return ''
	if filename == 'start.gcode':
		if extruderCount > 1:
			alterationContents = getAlterationFile("start%d.gcode" % (extruderCount))
		#For the start code, hack the temperature and the steps per E value into it. So the temperature is reached before the start code extrusion.
		#We also set our steps per E here, if configured.
		eSteps = getMachineSettingFloat('steps_per_e')
		if eSteps > 0:
			prefix += 'M92 E%f\n' % (eSteps)
		temp = getProfileSettingFloat('print_temperature')
		bedTemp = 0
		if getMachineSetting('has_heated_bed') == 'True':
			bedTemp = getProfileSettingFloat('print_bed_temperature')

		if bedTemp > 0 and not isTagIn('{print_bed_temperature}', alterationContents):
			prefix += 'M140 S%f\n' % (bedTemp)
		if temp > 0 and not isTagIn('{print_temperature}', alterationContents):
			if extruderCount > 0:
				for n in xrange(1, extruderCount):
					t = temp
					if n > 0 and getProfileSettingFloat('print_temperature%d' % (n+1)) > 0:
						t = getProfileSettingFloat('print_temperature%d' % (n+1))
					prefix += 'M104 T%d S%f\n' % (n, t)
				for n in xrange(0, extruderCount):
					t = temp
					if n > 0 and getProfileSettingFloat('print_temperature%d' % (n+1)) > 0:
						t = getProfileSettingFloat('print_temperature%d' % (n+1))
					prefix += 'M109 T%d S%f\n' % (n, t)
				prefix += 'T0\n'
			else:
				prefix += 'M109 S%f\n' % (temp)
		if bedTemp > 0 and not isTagIn('{print_bed_temperature}', alterationContents):
			prefix += 'M190 S%f\n' % (bedTemp)
	elif filename == 'end.gcode':
		if extruderCount > 1:
			alterationContents = getAlterationFile("end%d.gcode" % (extruderCount))
		#Append the profile string to the end of the GCode, so we can load it from the GCode file later.
		#postfix = ';CURA_PROFILE_STRING:%s\n' % (getProfileString())
	return unicode(prefix + re.sub("(.)\{([^\}]*)\}", replaceTagMatch, alterationContents).rstrip() + '\n' + postfix).strip().encode('utf-8') + '\n'


 * 	 float layer_height=0.1f; 
	 float wall_thickness=0.8f;
	 boolean retraction_enable=true;
	 float solid_layer_thickness=0.6f;
	 float fill_density=20f;
	 float print_speed=50f;
 <Spinner     android:tag="support"
 <Spinner  android:tag="platform_adhesion"
"nozzle_size="0.4"
bottom_thickness="0.3" 
="object_sink="0.0" 
overlap_dual"  android:text="0.15"
 <EditText  android:tag="travel_speed"  android:text="150"
 <EditText  android:tag="bottom_layer_speed"  android:text="20"
 <EditText  android:tag="infill_speed"  android:text="0
 <EditText  android:tag="inset0_speed"  android:text="0"
 <EditText  android:tag="insetx_speed"  android:text="0"
 <EditText   android:tag="cool_min_layer_time"  android:text="5"
 <CheckBox   android:tag="fan_enabled" "true"
*/

}
