package lkj.unicron.cura;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Process;

public class Setting {
	public static HashMap <String, Setting> gSettingsDictionary= new HashMap<String,Setting>();
	public static ArrayList <Setting> gSettingsList = new ArrayList <Setting>();
	
	private String _name ;
	private String _label ;
	private String _tooltip ;
	private String _default ; //default value
	private String _value ;  //user set value
	//private ArrayList<String> _values ;
	private String _type ;
	private String _category ; //isPreference, isMachineSetting
	private String _subcategory ;
	private String _minValue ;
	private String _maxValue ;
	private String _conditions ;
	
	public static void AddSetting(Setting s){
		if(!gSettingsDictionary.containsKey(s.getName())){
			gSettingsDictionary.put(s.getName(), s);
			gSettingsList.add(s);
		}
	}
	
	//default value, condition check,
	
	public void InitSettings(){
		        AddSetting(
				setting("layer_height", "0.1", "float", "basic", "Quality").setRange("0.0001").setLabel("Layer height (mm)", "Layer height in millimeters.\nThis is the most important setting to determine the quality of your print. Normal quality prints are 0.1mm, high quality is 0.06mm. You can go up to 0.25mm with an Ultimaker for very fast prints at low quality.")
				);
				AddSetting(
				setting("wall_thickness",            "0.8", "float", "basic",    "Quality").setRange("0.0").setLabel("Shell thickness (mm)", "Thickness of the outside shell in the horizontal direction.\nThis is used in combination with the nozzle size to define the number\nof perimeter lines and the thickness of those perimeter lines.")
				);
				AddSetting(
				setting("retraction_enable",        "True", "bool",  "basic",    "Quality").setLabel("Enable retraction", "Retract the filament when the nozzle is moving over a none-printed area. Details about the retraction can be configured in the advanced tab.")
				);
				AddSetting(
				setting("solid_layer_thickness",     "0.6", "float", "basic",    "Fill").setRange("0").setLabel("Bottom/Top thickness (mm)", "This controls the thickness of the bottom and top layers, the amount of solid layers put down is calculated by the layer thickness and this value.\nHaving this value a multiple of the layer thickness makes sense. And keep it near your wall thickness to make an evenly strong part.")
				);
				AddSetting(
				setting("fill_density",               "20", "float", "basic",    "Fill").setRange("0", "100").setLabel("Fill Density (%)", "This controls how densely filled the insides of your print will be. For a solid part use 100%, for an empty part use 0%. A value around 20% is usually enough.\nThis won\"t affect the outside of the print and only adjusts how strong the part becomes.")
				);
				AddSetting(
				setting("nozzle_size",               "0.4", "float", "advanced", "Machine").setRange("0.1","10").setLabel("Nozzle size (mm)", "The nozzle size is very important, this is used to calculate the line width of the infill, and used to calculate the amount of outside wall lines and thickness for the wall thickness you entered in the print settings.")
				);
				AddSetting(
				setting("print_speed",                "50", "float", "basic",    "Speed and Temperature").setRange("1").setLabel("Print speed (mm/s)", "Speed at which printing happens. A well adjusted Ultimaker can reach 150mm/s, but for good quality prints you want to print slower. Printing speed depends on a lot of factors. So you will be experimenting with optimal settings for this.")
				);
				AddSetting(
				setting("print_temperature",         "220", "int",   "basic",    "Speed and Temperature").setRange("0","340").setLabel("Printing temperature (C)", "Temperature used for printing. Set at 0 to pre-heat yourself.\nFor PLA a value of 210C is usually used.\nFor ABS a value of 230C or higher is required.")
				);
				AddSetting(
				setting("print_temperature2",          "0", "int",   "basic",    "Speed and Temperature").setRange("0","340").setLabel("2nd nozzle temperature (C)", "Temperature used for printing. Set at 0 to pre-heat yourself.\nFor PLA a value of 210C is usually used.\nFor ABS a value of 230C or higher is required.")
				);
				AddSetting(
				setting("print_temperature3",          "0", "int",   "basic",    "Speed and Temperature").setRange("0","340").setLabel("3th nozzle temperature (C)", "Temperature used for printing. Set at 0 to pre-heat yourself.\nFor PLA a value of 210C is usually used.\nFor ABS a value of 230C or higher is required.")
				);
				AddSetting(
				setting("print_temperature4",          "0", "int",   "basic",    "Speed and Temperature").setRange("0","340").setLabel("4th nozzle temperature (C)", "Temperature used for printing. Set at 0 to pre-heat yourself.\nFor PLA a value of 210C is usually used.\nFor ABS a value of 230C or higher is required.")
				);
				AddSetting(
				setting("print_bed_temperature",      "70", "int",   "basic",    "Speed and Temperature").setRange("0","340").setLabel("Bed temperature (C\")", "Temperature used for the heated printer bed. Set at 0 to pre-heat yourself.")
				);
				AddSetting(
				setting("support",                "None", "None,Touching buildplate,Everywhere", "basic", "Support").setLabel("Support type", "Type of support structure build.\n\"Touching buildplate\" is the most commonly used support setting.\n\nNone does not do any support.\nTouching buildplate only creates support where the support structure will touch the build platform.\nEverywhere creates support even on top of parts of the model.")
				);
				AddSetting(
				setting("platform_adhesion",      "None",  "None,Brim,Raft", "basic", "Support").setLabel("Platform adhesion type", "Different options that help in preventing corners from lifting due to warping.\nBrim adds a single layer thick flat area around your object which is easy to cut off afterwards, and it is the recommended option.\nRaft adds a thick raster below the object and a thin interface between this and your object.\n(Note that enabling the brim or raft disables the skirt)")
				);
				AddSetting(
				setting("support_dual_extrusion",  "Both", "Both,First extruder,Second extruder", "basic", "Support").setLabel("Support dual extrusion", "Which extruder to use for support material, for break-away support you can use both extruders.\nBut if one of the materials is more expensive then the other you could select an extruder to use for support material. This causes more extruder switches.\nYou can also use the 2nd extruder for soluble support materials.")
				);
				AddSetting(
				setting("wipe_tower",              "False", "bool",  "basic",    "Dual extrusion").setLabel("Wipe&prime tower", "The wipe-tower is a tower printed on every layer when switching between nozzles.\nThe old nozzle is wiped off on the tower before the new nozzle is used to print the 2nd color.")
				);
				AddSetting(
				setting("wipe_tower_volume",          "15", "float", "expert",   "Dual extrusion").setLabel("Wipe&prime tower volume per layer (mm3)", "The amount of material put in the wipe/prime tower.\nThis is done in volume because in general you want to extrude a\ncertain amount of volume to get the extruder going, independent on the layer height.\nThis means that with thinner layers, your tower gets bigger.")
				);
				
				
				AddSetting(
						setting("ooze_shield",             "False", "bool",  "basic",    "Dual extrusion").setLabel("Ooze shield", "The ooze shield is a 1 line thick shell around the object which stands a few mm from the object.\nThis shield catches any oozing from the unused nozzle in dual-extrusion.")
						);
						AddSetting(
						setting("filament_diameter",        "2.85", "float", "basic",    "Filament").setRange("1").setLabel("Diameter (mm)", "Diameter of your filament, as accurately as possible.\nIf you cannot measure this value you will have to calibrate it, a higher number means less extrusion, a smaller number generates more extrusion.")
						);
						AddSetting(
						setting("filament_diameter2",          "0", "float", "basic",    "Filament").setRange("0").setLabel("Diameter2 (mm)", "Diameter of your filament for the 2nd nozzle. Use 0 to use the same diameter as for nozzle 1.")
						);
						AddSetting(
						setting("filament_diameter3",          "0", "float", "basic",    "Filament").setRange("0").setLabel("Diameter3 (mm)", "Diameter of your filament for the 3th nozzle. Use 0 to use the same diameter as for nozzle 1.")
						);
						AddSetting(
						setting("filament_diameter4",          "0", "float", "basic",    "Filament").setRange("0").setLabel("Diameter4 (mm)", "Diameter of your filament for the 4th nozzle. Use 0 to use the same diameter as for nozzle 1.")
						);
						AddSetting(
						setting("filament_flow",            "100", "float", "basic",    "Filament").setRange("5","300").setLabel("Flow (%)", "Flow compensation, the amount of material extruded is multiplied by this value")
						);
						AddSetting(
						setting("retraction_speed",         "40.0", "float", "advanced", "Retraction").setRange("0.1").setLabel("Speed (mm/s)", "Speed at which the filament is retracted, a higher retraction speed works better. But a very high retraction speed can lead to filament grinding.")
						);
						AddSetting(
						setting("retraction_amount",         "4.5", "float", "advanced", "Retraction").setRange("0").setLabel("Distance (mm)", "Amount of retraction, set at 0 for no retraction at all. A value of 4.5mm seems to generate good results.")
						);
						AddSetting(
						setting("retraction_dual_amount",   "16.5", "float", "advanced", "Retraction").setRange("0").setLabel("Dual extrusion switch amount (mm)", "Amount of retraction when switching nozzle with dual-extrusion, set at 0 for no retraction at all. A value of 16.0mm seems to generate good results.")
						);
						AddSetting(
						setting("retraction_min_travel",     "1.5", "float", "expert",   "Retraction").setRange("0").setLabel("Minimum travel (mm)", "Minimum amount of travel needed for a retraction to happen at all. To make sure you do not get a lot of retractions in a small area.")
						);
						AddSetting(
						setting("retraction_combing",       "True", "bool",  "expert",   "Retraction").setLabel("Enable combing", "Combing is the act of avoiding holes in the print for the head to travel over. If combing is disabled the printer head moves straight from the start point to the end point and it will always retract.")
						);
						AddSetting(
						setting("retraction_minimal_extrusion", "0.02", "float","expert", "Retraction").setRange("0").setLabel("Minimal extrusion before retracting (mm)", "The minimal amount of extrusion that needs to be done before retracting again if a retraction needs to happen before this minimal is reached the retraction is ignored.\nThis avoids retracting a lot on the same piece of filament which flattens the filament and causes grinding issues.")
						);
						AddSetting(
						setting("retraction_hop",            "0.0", "float", "expert",   "Retraction").setRange("0").setLabel("Z hop when retracting (mm)", "When a retraction is done, the head is lifted by this amount to travel over the print. A value of 0.075 works well. This feature has a lot of positive effect on delta towers.")
						);
						AddSetting(
						setting("bottom_thickness",          "0.3", "float", "advanced", "Quality").setRange("0").setLabel("Initial layer thickness (mm)", "Layer thickness of the bottom layer. A thicker bottom layer makes sticking to the bed easier. Set to 0.0 to have the bottom layer thickness the same as the other layers.")
						);
						AddSetting(
						setting("object_sink",               "0.0", "float", "advanced", "Quality").setRange("0").setLabel("Cut off object bottom (mm)", "Sinks the object into the platform, this can be used for objects that do not have a flat bottom and thus create a too small first layer.")
						);
						AddSetting(
						setting("overlap_dual",             "0.15", "float", "advanced", "Quality").setLabel("Dual extrusion overlap (mm)", "Add a certain amount of overlapping extrusion on dual-extrusion prints. This bonds the different colors together.")
						);
						
						AddSetting(
								setting("travel_speed",            "150.0", "float", "advanced", "Speed").setRange("0.1").setLabel("Travel speed (mm/s)", "Speed at which travel moves are done, a well built Ultimaker can reach speeds of 250mm/s. But some machines might miss steps then.")
								);
								AddSetting(
								setting("bottom_layer_speed",         "20", "float", "advanced", "Speed").setRange("0.1").setLabel("Bottom layer speed (mm/s)", "Print speed for the bottom layer, you want to print the first layer slower so it sticks better to the printer bed.")
								);
								AddSetting(
								setting("infill_speed",              "0.0", "float", "advanced", "Speed").setRange("0.0").setLabel("Infill speed (mm/s)", "Speed at which infill parts are printed. If set to 0 then the print speed is used for the infill. Printing the infill faster can greatly reduce printing time, but this can negatively affect print quality.")
								);
								AddSetting(
								setting("inset0_speed",              "0.0", "float", "advanced", "Speed").setRange("0.0").setLabel("Outer shell speed (mm/s)", "Speed at which outer shell is printed. If set to 0 then the print speed is used. Printing the outer shell at a lower speed improves the final skin quality. However, having a large difference between the inner shell speed and the outer shell speed will effect quality in a negative way.")
								);
								AddSetting(
								setting("insetx_speed",              "0.0", "float", "advanced", "Speed").setRange("0.0").setLabel("Inner shell speed (mm/s)", "Speed at which inner shells are printed. If set to 0 then the print speed is used. Printing the inner shell faster then the outer shell will reduce printing time. It is good to set this somewhere in between the outer shell speed and the infill/printing speed.")
								);
								AddSetting(
								setting("cool_min_layer_time",         "5", "float", "advanced", "Cool").setRange("0").setLabel("Minimal layer time (sec)", "Minimum time spent in a layer, gives the layer time to cool down before the next layer is put on top. If the layer will be placed down too fast the printer will slow down to make sure it has spent at least this amount of seconds printing this layer.")
								);
								AddSetting(
								setting("fan_enabled",              "True", "bool",  "advanced", "Cool").setLabel("Enable cooling fan", "Enable the cooling fan during the print. The extra cooling from the cooling fan is essential during faster prints.")
								);
								AddSetting(

								setting("skirt_line_count",            "1", "int",   "expert", "Skirt").setRange("0").setLabel("Line count", "The skirt is a line drawn around the object at the first layer. This helps to prime your extruder, and to see if the object fits on your platform.\nSetting this to 0 will disable the skirt. Multiple skirt lines can help priming your extruder better for small objects.")
								);
								AddSetting(
								setting("skirt_gap",                 "3.0", "float", "expert", "Skirt").setRange("0").setLabel("Start distance (mm)", "The distance between the skirt and the first layer.\nThis is the minimal distance, multiple skirt lines will be put outwards from this distance.")
								);
								AddSetting(
								setting("skirt_minimal_length",    "150.0", "float", "expert", "Skirt").setRange("0").setLabel("Minimal length (mm)", "The minimal length of the skirt, if this minimal length is not reached it will add more skirt lines to reach this minimal lenght.\nNote: If the line count is set to 0 this is ignored.")
								);
								AddSetting(
								setting("fan_full_height",           "0.5", "float", "expert",   "Cool").setRange("0").setLabel("Fan full on at height (mm)", "The height at which the fan is turned on completely. For the layers below this the fan speed is scaled linearly with the fan off at layer 0.")
								);
								AddSetting(
								setting("fan_speed",                 "100", "int",   "expert",   "Cool").setRange("0","100").setLabel("Fan speed min (%)", "When the fan is turned on, it is enabled at this speed setting. If cool slows down the layer, the fan is adjusted between the min and max speed. Minimal fan speed is used if the layer is not slowed down due to cooling.")
								);
								AddSetting(
								setting("fan_speed_max",             "100", "int",   "expert",   "Cool").setRange("0","100").setLabel("Fan speed max (%)", "When the fan is turned on, it is enabled at this speed setting. If cool slows down the layer, the fan is adjusted between the min and max speed. Maximal fan speed is used if the layer is slowed down due to cooling by more than 200%.")
								);
								AddSetting(
								setting("cool_min_feedrate",          "10", "float", "expert",   "Cool").setRange("0").setLabel("Minimum speed (mm/s)", "The minimal layer time can cause the print to slow down so much it starts to ooze. The minimal feedrate protects against this. Even if a print gets slowed down it will never be slower than this minimal speed.")
								);
								AddSetting(
								setting("cool_head_lift",          "False", "bool",  "expert",   "Cool").setLabel("Cool head lift", "Lift the head if the minimal speed is hit because of cool slowdown, and wait the extra time so the minimal layer time is always hit.")
								);
								AddSetting(
								setting("solid_top", "True", "bool", "expert", "Infill").setLabel("Solid infill top", "Create a solid top surface, if set to false the top is filled with the fill percentage. Useful for cups/vases.")
								);
								AddSetting(
								setting("solid_bottom", "True", "bool", "expert", "Infill").setLabel("Solid infill bottom", "Create a solid bottom surface, if set to false the bottom is filled with the fill percentage. Useful for buildings.")
								);
								
								AddSetting(
										setting("fill_overlap", "15", "int", "expert", "Infill").setRange("0","100").setLabel("Infill overlap (%)", "Amount of overlap between the infill and the walls. There is a slight overlap with the walls and the infill so the walls connect firmly to the infill.")
										);
										AddSetting(
										setting("support_type", "Grid", "Grid,Lines", "expert", "Support").setLabel("Structure type", "The type of support structure.\nGrid is very strong and can come off in 1 piece, however, sometimes it is too strong.\nLines are single walled lines that break off one at a time. Which is more work to remove, but as it is less strong it does work better on tricky prints.")
										);
										AddSetting(
										setting("support_angle", "60", "float", "expert", "Support").setRange("0","90").setLabel("Overhang angle for support (deg)", "The minimal angle that overhangs need to have to get support. With 0 degree being horizontal and 90 degree being vertical.")
										);
										AddSetting(
										setting("support_fill_rate", "15", "int", "expert", "Support").setRange("0","100").setLabel("Fill amount (%)", "Amount of infill structure in the support material, less material gives weaker support which is easier to remove. 15% seems to be a good average.")
										);
										AddSetting(
										setting("support_xy_distance", "0.7", "float", "expert", "Support").setRange("0","10").setLabel("Distance X/Y (mm)", "Distance of the support material from the print, in the X/Y directions.\n0.7mm gives a nice distance from the print so the support does not stick to the print.")
										);
										AddSetting(
										setting("support_z_distance", "0.15", "float", "expert", "Support").setRange("0","10").setLabel("Distance Z (mm)", "Distance from the top/bottom of the support to the print. A small gap here makes it easier to remove the support but makes the print a bit uglier.\n0.15mm gives a good seperation of the support material.")
										);
										AddSetting(
										setting("spiralize", "False", "bool", "expert", "Spiralize").setLabel("Spiralize the outer contour", "Spiralize is smoothing out the Z move of the outer edge. This will create a steady Z increase over the whole print. This feature turns a solid object into a single walled print with a solid bottom.")
										);
										AddSetting(
										setting("brim_line_count", "20", "int", "expert", "Brim").setRange("1100").setLabel("Brim line amount", "The amount of lines used for a brim, more lines means a larger brim which sticks better, but this also makes your effective print area smaller.")
										);
										AddSetting(
										setting("raft_margin", "5.0", "float", "expert", "Raft").setRange("0").setLabel("Extra margin (mm)", "If the raft is enabled, this is the extra raft area around the object which is also rafted. Increasing this margin will create a stronger raft while using more material and leaving less area for your print.")
										);
										AddSetting(
										setting("raft_line_spacing", "3.0", "float", "expert", "Raft").setRange("0").setLabel("Line spacing (mm)", "When you are using the raft this is the distance between the centerlines of the raft line.")
										);
										AddSetting(
										setting("raft_base_thickness", "0.3", "float", "expert", "Raft").setRange("0").setLabel("Base thickness (mm)", "When you are using the raft this is the thickness of the base layer which is put down.")
										);
										AddSetting(
										setting("raft_base_linewidth", "1.0", "float", "expert", "Raft").setRange("0").setLabel("Base line width (mm)", "When you are using the raft this is the width of the base layer lines which are put down.")
										);
										AddSetting(
										setting("raft_interface_thickness", "0.27", "float", "expert", "Raft").setRange("0").setLabel("Interface thickness (mm)", "When you are using the raft this is the thickness of the interface layer which is put down.")
										);
										AddSetting(
										setting("raft_interface_linewidth", "0.4", "float", "expert", "Raft").setRange("0").setLabel("Interface line width (mm)", "When you are using the raft this is the width of the interface layer lines which are put down.")
										);
										AddSetting(
										setting("raft_airgap", "0.22", "float", "expert", "Raft").setRange("0").setLabel("Airgap", "Gap between the last layer of the raft and the first printing layer. A small gap of 0.2mm works wonders on PLA and makes the raft easy to remove.")
										);
										AddSetting(
										setting("raft_surface_layers", "2", "int", "expert", "Raft").setRange("0").setLabel("Surface layers", "Amount of surface layers put on top of the raft, these are fully filled layers on which the model is printed.")
										);
										

						AddSetting(
						setting("fix_horrible_union_all_type_a", "True",  "bool", "expert", "Fix horrible").setLabel("Combine everything (Type-A)", "This expert option adds all parts of the model together. The result is usually that internal cavities disappear. Depending on the model this can be intended or not. Enabling this option is at your own risk. Type-A is dependent on the model normals and tries to keep some internal holes intact. Type-B ignores all internal holes and only keeps the outside shape per layer.")
						);
						AddSetting(
						setting("fix_horrible_union_all_type_b", "False", "bool", "expert", "Fix horrible").setLabel("Combine everything (Type-B)", "This expert option adds all parts of the model together. The result is usually that internal cavities disappear. Depending on the model this can be intended or not. Enabling this option is at your own risk. Type-A is dependent on the model normals and tries to keep some internal holes intact. Type-B ignores all internal holes and only keeps the outside shape per layer.")
						);
						AddSetting(
						setting("fix_horrible_use_open_bits", "False", "bool", "expert", "Fix horrible").setLabel("Keep open faces", "This expert option keeps all the open bits of the model intact. Normally Cura tries to stitch up small holes and remove everything with big holes, but this option keeps bits that are not properly part of anything and just goes with whatever is left. This option is usually not what you want, but it might enable you to slice models otherwise failing to produce proper paths.\nAs with all \"Fix horrible\" options, results may vary and use at your own risk.")
						);
						AddSetting(
						setting("fix_horrible_extensive_stitching", "False", "bool", "expert", "Fix horrible").setLabel("Extensive stitching", "Extensive stitching tries to fix up open holes in the model by closing the hole with touching polygons. This algorthm is quite expensive and could introduce a lot of processing time.\nAs with all \"Fix horrible\" options, results may vary and use at your own risk.")
						);
						AddSetting(
						
						setting("plugin_config", "", "string", "hidden", "hidden")
						);
						AddSetting(
						setting("object_center_x", "-1", "float", "hidden", "hidden")
						);
						AddSetting(
						setting("object_center_y", "-1", "float", "hidden", "hidden")
						);
						
						AddSetting(
								setting("start.gcode", 
								/*;Sliced at: {day} {date} {time}
								;Basic settings: Layer height: {layer_height} Walls: {wall_thickness} Fill: {fill_density}
								;Print time: {print_time}
								;Filament used: {filament_amount}m {filament_weight}g
								;Filament cost: {filament_cost}
								;M190 S{print_bed_temperature} ;Uncomment to add your own bed temperature line
								;M109 S{print_temperature} ;Uncomment to add your own temperature line
								*/
								"G21 G90 M82 M107 G28 X0 Y0 G28 Z0 G1 Z15.0 F{travel_speed} G92 E0  G1 F200 E3	G92 E0 	G1 F{travel_speed} M117 Printing...", 
								"string", "alteration", "alteration")
								);
								AddSetting(
								setting("end.gcode", 
								"M104 S0 M140 S0 G91	G1 E-1 F300	G1 Z+0.5 E-5 X-20 Y-20 F{travel_speed} G28 X0 Y0 M84 G90",
								"string", "alteration", "alteration")
								);
								AddSetting(
								setting("start2.gcode", 
								"G21 G90 M107 G28 X0 Y0 G28 Z0 G1 Z15.0 F{travel_speed} T1 G92 E0  G1 F200 E10 G92 E0 G1 F200 E-{retraction_dual_amount} T0 G92 E0 G1 F200 E10 G92 E0 G1 F{travel_speed} M117 Printing..."
								, "string", "alteration", "alteration")
								);
								AddSetting(
								setting("end2.gcode", 
								"M104 T0 S0 M104 T1 S0 M140 S0  G91 G1 E-1 F300 G1 Z+0.5 E-5 X-20 Y-20 F{travel_speed} G28 X0 Y0 M84 G90 "
								, "string", "alteration", "alteration")
								);
								AddSetting(
								setting("support_start.gcode", "", "string", "alteration", "alteration")
								);
								AddSetting(
								setting("support_end.gcode", "", "string", "alteration", "alteration")
								);
								AddSetting(
								setting("cool_start.gcode", "", "string", "alteration", "alteration")
								);
								AddSetting(
								setting("cool_end.gcode", "", "string", "alteration", "alteration")
								);
								AddSetting(
								setting("replace.csv", "", "string", "alteration", "alteration")
								);
								AddSetting(
								setting("switchExtruder.gcode",
								"G92 E0	G1 E-36 F5000 G92 E0 T{extruder} G1 X{new_x} Y{new_y} Z{new_z} F{travel_speed} G1 E36 F5000 G92 E0 "
								, "string", "alteration", "alteration")
								);
								AddSetting(
								setting("startMode", "Simple", "Simple,Normal", "preference", "hidden")
								);
								AddSetting(
								setting("oneAtATime", "True", "bool", "preference", "hidden")
								);
								AddSetting(
								setting("lastFile", "resources,example,UltimakerRobot_support.stl", "string", "preference", "hidden")
								);
								AddSetting(
								setting("save_profile", "False", "bool", "preference", "hidden").setLabel("Save profile on slice", "When slicing save the profile as [stl_file]_profile.ini next to the model.")
								);
								AddSetting(
								setting("filament_cost_kg", "0", "float", "preference", "hidden").setLabel("Cost (price/kg)", "Cost of your filament per kg, to estimate the cost of the final print.")
								);
								AddSetting(
								setting("filament_cost_meter", "0", "float", "preference", "hidden").setLabel("Cost (price/m)", "Cost of your filament per meter, to estimate the cost of the final print.")
								);
								AddSetting(
								setting("auto_detect_sd", "True", "bool", "preference", "hidden").setLabel("Auto detect SD card drive", "Auto detect the SD card. You can disable this because on some systems external hard-drives or USB sticks are detected as SD card.")
								);
								AddSetting(
								setting("check_for_updates", "True", "bool", "preference", "hidden").setLabel("Check for updates", "Check for newer versions of Cura on startup")
								);
								AddSetting(
								setting("submit_slice_information", "False", "bool", "preference", "hidden").setLabel("Send usage statistics", "Submit anonymous usage information to improve future versions of Cura")
								);
								AddSetting(
								setting("youmagine_token", "", "string", "preference", "hidden")
								);
								AddSetting(
								setting("filament_physical_density", "1240", "float", "preference", "hidden").setRange("500.0", "3000.0").setLabel("Density (kg/m3)", "Weight of the filament per m3. Around 1240 for PLA. And around 1040 for ABS. This value is used to estimate the weight if the filament used for the print.")
								);
								AddSetting(
								setting("language", "English", "string", "preference", "hidden").setLabel("Language", "Change the language in which Cura runs. Switching language requires a restart of Cura")
								);
								AddSetting(
								setting("active_machine", "0", "int", "preference", "hidden")
								);
								AddSetting(

								setting("model_colour", "#FFC924", "string", "preference", "hidden").setLabel("Model colour", "Display color for first extruder")
								);
								AddSetting(
								setting("model_colour2", "#CB3030", "string", "preference", "hidden").setLabel("Model colour (2)", "Display color for second extruder")
								);
								AddSetting(
								setting("model_colour3", "#DDD93C", "string", "preference", "hidden").setLabel("Model colour (3)", "Display color for third extruder")
								);
								AddSetting(
								setting("model_colour4", "#4550D3", "string", "preference", "hidden").setLabel("Model colour (4)", "Display color for forth extruder")
								);
								AddSetting(
								setting("printing_window", "Basic", "string", "preference", "hidden").setLabel("Printing window type", "Select the interface used for USB printing.")
								);
								AddSetting(

								setting("window_maximized", "True", "bool", "preference", "hidden")
								);
								AddSetting(
								setting("window_pos_x", "-1", "float", "preference", "hidden")
								);
								AddSetting(
								setting("window_pos_y", "-1", "float", "preference", "hidden")
								);
								AddSetting(
								setting("window_width", "-1", "float", "preference", "hidden")
								);
								AddSetting(
								setting("window_height", "-1", "float", "preference", "hidden")
								);
								AddSetting(
								setting("window_normal_sash", "320", "float", "preference", "hidden")
								);
								AddSetting(
								setting("last_run_version", "", "string", "preference", "hidden")
								);
								AddSetting(

								setting("machine_name", "", "string", "machine", "hidden")
								);
								AddSetting(
								setting("machine_type", "unknown", "string", "machine", "hidden")
								);
								AddSetting(
								setting("machine_width", "205", "float", "machine", "hidden").setLabel("Maximum width (mm)", "Size of the machine in mm")
								);
								AddSetting(
								setting("machine_depth", "205", "float", "machine", "hidden").setLabel("Maximum depth (mm)", "Size of the machine in mm")
								);
								AddSetting(
								setting("machine_height", "200", "float", "machine", "hidden").setLabel("Maximum height (mm)", "Size of the machine in mm")
								);
								AddSetting(
								setting("machine_center_is_zero", "False", "bool", "machine", "hidden").setLabel("Machine center 0,0", "Machines firmware defines the center of the bed as 0,0 instead of the front left corner.")
								);
								AddSetting(
								setting("machine_shape", "Square", "Square,Circular", "machine", "hidden").setLabel("Build area shape", "The shape of machine build area.")
								);
								AddSetting(
								setting("ultimaker_extruder_upgrade", "False", "bool", "machine", "hidden")
								);
								AddSetting(
								setting("has_heated_bed", "False", "bool", "machine", "hidden").setLabel("Heated bed", "If you have an heated bed, this enabled heated bed settings (requires restart)")
								);
								AddSetting(
								setting("gcode_flavor", "RepRap (Marlin/Sprinter),RepRap (Marlin/Sprinter),RepRap (Volumetric),UltiGCode,MakerBot,BFB,Mach3", "string", "machine", "hidden").setLabel("GCode Flavor", "Flavor of generated GCode.\nRepRap is normal 5D GCode which works on Marlin/Sprinter based firmwares.\nUltiGCode is a variation of the RepRap GCode which puts more settings in the machine instead of the slicer.\nMakerBot GCode has a few changes in the way GCode is generated, but still requires MakerWare to generate to X3G.\nBFB style generates RPM based code.\nMach3 uses A,B,C instead of E for extruders.")
								);
								AddSetting(
								setting("extruder_amount", "1", "1,2,3,4", "machine", "hidden").setLabel("Extruder count", "Amount of extruders in your machine.")
								);
								AddSetting(
								setting("extruder_offset_x1", "0.0", "float", "machine", "hidden").setLabel("Offset X", "The offset of your secondary extruder compared to the primary.")
								);
								AddSetting(
								setting("extruder_offset_y1", "21.6", "float", "machine", "hidden").setLabel("Offset Y", "The offset of your secondary extruder compared to the primary.")
								);
								AddSetting(
								setting("extruder_offset_x2", "0.0", "float", "machine", "hidden").setLabel("Offset X", "The offset of your tertiary extruder compared to the primary.")
								);
								AddSetting(
								setting("extruder_offset_y2", "0.0", "float", "machine", "hidden").setLabel("Offset Y", "The offset of your tertiary extruder compared to the primary.")
								);
								AddSetting(
								setting("extruder_offset_x3", "0.0", "float", "machine", "hidden").setLabel("Offset X", "The offset of your forth extruder compared to the primary.")
								);
								AddSetting(
								setting("extruder_offset_y3", "0.0", "float", "machine", "hidden").setLabel("Offset Y", "The offset of your forth extruder compared to the primary.")
								);
								AddSetting(
								setting("steps_per_e", "0", "float", "machine", "hidden").setLabel("E-Steps per 1mm filament", "Amount of steps per mm filament extrusion. If set to 0 then this value is ignored and the value in your firmware is used.")
								);
								AddSetting(
								setting("serial_port", "AUTO", "string", "machine", "hidden").setLabel("Serial port", "Serial port to use for communication with the printer")
								);
								AddSetting(
								setting("serial_port_auto", "", "string", "machine", "hidden")
								);
								AddSetting(
								setting("serial_baud", "AUTO", "string", "machine", "hidden").setLabel("Baudrate", "Speed of the serial port communication\nNeeds to match your firmware settings\nCommon values are 250000, 115200, 57600")
								);
								AddSetting(
								setting("serial_baud_auto", "", "int", "machine", "hidden")
								);
								AddSetting(

								setting("extruder_head_size_min_x", "0.0", "float", "machine", "hidden").setLabel("Head size towards X min (mm)", "The head size when printing multiple objects, measured from the tip of the nozzle towards the outer part of the head. 75mm for an Ultimaker if the fan is on the left side.")
								);
								AddSetting(
								setting("extruder_head_size_min_y", "0.0", "float", "machine", "hidden").setLabel("Head size towards Y min (mm)", "The head size when printing multiple objects, measured from the tip of the nozzle towards the outer part of the head. 18mm for an Ultimaker if the fan is on the left side.")
								);
								AddSetting(
								setting("extruder_head_size_max_x", "0.0", "float", "machine", "hidden").setLabel("Head size towards X max (mm)", "The head size when printing multiple objects, measured from the tip of the nozzle towards the outer part of the head. 18mm for an Ultimaker if the fan is on the left side.")
								);
								AddSetting(
								setting("extruder_head_size_max_y", "0.0", "float", "machine", "hidden").setLabel("Head size towards Y max (mm)", "The head size when printing multiple objects, measured from the tip of the nozzle towards the outer part of the head. 35mm for an Ultimaker if the fan is on the left side.")
								);
								AddSetting(
								setting("extruder_head_size_height", "0.0", "float", "machine", "hidden").setLabel("Printer gantry height (mm)", "The height of the gantry holding up the printer head. If an object is higher then this then you cannot print multiple objects one for one. 60mm for an Ultimaker.")
								);
	}

	private Setting setting(String _name, String _default,
			String _type, String _category,
			String _subcategory) {
		// TODO Auto-generated method stub
		Setting setting = new Setting(_name, _default, _type, _category, _subcategory);
		return setting;
	}

	
	public static <T> String getType(T t){
		if (t instanceof Integer){
			return "I know";	
		}
		return "do not know";
	}
	
	public  Setting(String _name, String _default,
			String _type, String _category,
			String _subcategory) {
		this._name = _name;
		this._label = _name;
		this._tooltip = "";
		this._default = _default;
		this._value = null;
		this._type = _type;
		this._category = _category;
		this._subcategory = _subcategory;
		this._minValue = null;
		this._maxValue = null;
		//this._conditions = null;
		//return this;
	}
	

	public String getName() {
		return _name;
	}

	public String getLabel() {
		return _label;
	}
	
	public void setLabel(String _label) {
		setLabel( _label,"");
	}
	
	public Setting setLabel(String _label, String _tooltip) {
		this._label = _label;
		this._tooltip = _tooltip;
		return this;
	}
	
	public String getTooltip() {
		return _tooltip;
	}

	public String getDefault() {
		return _default;
	}

	public String getValue() {
		return _value;
	}
	
	public void setValue(String _value) {
		this._value = _value;
	}
	
	public Setting setRange(String minValue){
		setRange(minValue, minValue);
		return this;
	}
	
	public Setting setRange(String minValue, String maxValue){
		_minValue = minValue;
		_maxValue = maxValue;
		return this;
	}
				
	public String getType() {
		return _type;
	}

	public String getCategory() {
		return _category;
	}
	
	public String getSubcategory() {
		return _subcategory;
	}
	
	
	public void setSubcategory(String _subcategory) {
		this._subcategory = _subcategory;
	}

	public String getConditions() {
		return _conditions;
	}
	
	public void setConditions(String _conditions) {
		this._conditions = _conditions;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}

