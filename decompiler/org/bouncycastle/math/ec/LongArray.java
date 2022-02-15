package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

class LongArray implements Cloneable {
  private static final short[] INTERLEAVE2_TABLE = new short[] { 
      0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 
      68, 69, 80, 81, 84, 85, 256, 257, 260, 261, 
      272, 273, 276, 277, 320, 321, 324, 325, 336, 337, 
      340, 341, 1024, 1025, 1028, 1029, 1040, 1041, 1044, 1045, 
      1088, 1089, 1092, 1093, 1104, 1105, 1108, 1109, 1280, 1281, 
      1284, 1285, 1296, 1297, 1300, 1301, 1344, 1345, 1348, 1349, 
      1360, 1361, 1364, 1365, 4096, 4097, 4100, 4101, 4112, 4113, 
      4116, 4117, 4160, 4161, 4164, 4165, 4176, 4177, 4180, 4181, 
      4352, 4353, 4356, 4357, 4368, 4369, 4372, 4373, 4416, 4417, 
      4420, 4421, 4432, 4433, 4436, 4437, 5120, 5121, 5124, 5125, 
      5136, 5137, 5140, 5141, 5184, 5185, 5188, 5189, 5200, 5201, 
      5204, 5205, 5376, 5377, 5380, 5381, 5392, 5393, 5396, 5397, 
      5440, 5441, 5444, 5445, 5456, 5457, 5460, 5461, 16384, 16385, 
      16388, 16389, 16400, 16401, 16404, 16405, 16448, 16449, 16452, 16453, 
      16464, 16465, 16468, 16469, 16640, 16641, 16644, 16645, 16656, 16657, 
      16660, 16661, 16704, 16705, 16708, 16709, 16720, 16721, 16724, 16725, 
      17408, 17409, 17412, 17413, 17424, 17425, 17428, 17429, 17472, 17473, 
      17476, 17477, 17488, 17489, 17492, 17493, 17664, 17665, 17668, 17669, 
      17680, 17681, 17684, 17685, 17728, 17729, 17732, 17733, 17744, 17745, 
      17748, 17749, 20480, 20481, 20484, 20485, 20496, 20497, 20500, 20501, 
      20544, 20545, 20548, 20549, 20560, 20561, 20564, 20565, 20736, 20737, 
      20740, 20741, 20752, 20753, 20756, 20757, 20800, 20801, 20804, 20805, 
      20816, 20817, 20820, 20821, 21504, 21505, 21508, 21509, 21520, 21521, 
      21524, 21525, 21568, 21569, 21572, 21573, 21584, 21585, 21588, 21589, 
      21760, 21761, 21764, 21765, 21776, 21777, 21780, 21781, 21824, 21825, 
      21828, 21829, 21840, 21841, 21844, 21845 };
  
  private static final int[] INTERLEAVE3_TABLE = new int[] { 
      0, 1, 8, 9, 64, 65, 72, 73, 512, 513, 
      520, 521, 576, 577, 584, 585, 4096, 4097, 4104, 4105, 
      4160, 4161, 4168, 4169, 4608, 4609, 4616, 4617, 4672, 4673, 
      4680, 4681, 32768, 32769, 32776, 32777, 32832, 32833, 32840, 32841, 
      33280, 33281, 33288, 33289, 33344, 33345, 33352, 33353, 36864, 36865, 
      36872, 36873, 36928, 36929, 36936, 36937, 37376, 37377, 37384, 37385, 
      37440, 37441, 37448, 37449, 262144, 262145, 262152, 262153, 262208, 262209, 
      262216, 262217, 262656, 262657, 262664, 262665, 262720, 262721, 262728, 262729, 
      266240, 266241, 266248, 266249, 266304, 266305, 266312, 266313, 266752, 266753, 
      266760, 266761, 266816, 266817, 266824, 266825, 294912, 294913, 294920, 294921, 
      294976, 294977, 294984, 294985, 295424, 295425, 295432, 295433, 295488, 295489, 
      295496, 295497, 299008, 299009, 299016, 299017, 299072, 299073, 299080, 299081, 
      299520, 299521, 299528, 299529, 299584, 299585, 299592, 299593 };
  
  private static final int[] INTERLEAVE4_TABLE = new int[] { 
      0, 1, 16, 17, 256, 257, 272, 273, 4096, 4097, 
      4112, 4113, 4352, 4353, 4368, 4369, 65536, 65537, 65552, 65553, 
      65792, 65793, 65808, 65809, 69632, 69633, 69648, 69649, 69888, 69889, 
      69904, 69905, 1048576, 1048577, 1048592, 1048593, 1048832, 1048833, 1048848, 1048849, 
      1052672, 1052673, 1052688, 1052689, 1052928, 1052929, 1052944, 1052945, 1114112, 1114113, 
      1114128, 1114129, 1114368, 1114369, 1114384, 1114385, 1118208, 1118209, 1118224, 1118225, 
      1118464, 1118465, 1118480, 1118481, 16777216, 16777217, 16777232, 16777233, 16777472, 16777473, 
      16777488, 16777489, 16781312, 16781313, 16781328, 16781329, 16781568, 16781569, 16781584, 16781585, 
      16842752, 16842753, 16842768, 16842769, 16843008, 16843009, 16843024, 16843025, 16846848, 16846849, 
      16846864, 16846865, 16847104, 16847105, 16847120, 16847121, 17825792, 17825793, 17825808, 17825809, 
      17826048, 17826049, 17826064, 17826065, 17829888, 17829889, 17829904, 17829905, 17830144, 17830145, 
      17830160, 17830161, 17891328, 17891329, 17891344, 17891345, 17891584, 17891585, 17891600, 17891601, 
      17895424, 17895425, 17895440, 17895441, 17895680, 17895681, 17895696, 17895697, 268435456, 268435457, 
      268435472, 268435473, 268435712, 268435713, 268435728, 268435729, 268439552, 268439553, 268439568, 268439569, 
      268439808, 268439809, 268439824, 268439825, 268500992, 268500993, 268501008, 268501009, 268501248, 268501249, 
      268501264, 268501265, 268505088, 268505089, 268505104, 268505105, 268505344, 268505345, 268505360, 268505361, 
      269484032, 269484033, 269484048, 269484049, 269484288, 269484289, 269484304, 269484305, 269488128, 269488129, 
      269488144, 269488145, 269488384, 269488385, 269488400, 269488401, 269549568, 269549569, 269549584, 269549585, 
      269549824, 269549825, 269549840, 269549841, 269553664, 269553665, 269553680, 269553681, 269553920, 269553921, 
      269553936, 269553937, 285212672, 285212673, 285212688, 285212689, 285212928, 285212929, 285212944, 285212945, 
      285216768, 285216769, 285216784, 285216785, 285217024, 285217025, 285217040, 285217041, 285278208, 285278209, 
      285278224, 285278225, 285278464, 285278465, 285278480, 285278481, 285282304, 285282305, 285282320, 285282321, 
      285282560, 285282561, 285282576, 285282577, 286261248, 286261249, 286261264, 286261265, 286261504, 286261505, 
      286261520, 286261521, 286265344, 286265345, 286265360, 286265361, 286265600, 286265601, 286265616, 286265617, 
      286326784, 286326785, 286326800, 286326801, 286327040, 286327041, 286327056, 286327057, 286330880, 286330881, 
      286330896, 286330897, 286331136, 286331137, 286331152, 286331153 };
  
  private static final int[] INTERLEAVE5_TABLE = new int[] { 
      0, 1, 32, 33, 1024, 1025, 1056, 1057, 32768, 32769, 
      32800, 32801, 33792, 33793, 33824, 33825, 1048576, 1048577, 1048608, 1048609, 
      1049600, 1049601, 1049632, 1049633, 1081344, 1081345, 1081376, 1081377, 1082368, 1082369, 
      1082400, 1082401, 33554432, 33554433, 33554464, 33554465, 33555456, 33555457, 33555488, 33555489, 
      33587200, 33587201, 33587232, 33587233, 33588224, 33588225, 33588256, 33588257, 34603008, 34603009, 
      34603040, 34603041, 34604032, 34604033, 34604064, 34604065, 34635776, 34635777, 34635808, 34635809, 
      34636800, 34636801, 34636832, 34636833, 1073741824, 1073741825, 1073741856, 1073741857, 1073742848, 1073742849, 
      1073742880, 1073742881, 1073774592, 1073774593, 1073774624, 1073774625, 1073775616, 1073775617, 1073775648, 1073775649, 
      1074790400, 1074790401, 1074790432, 1074790433, 1074791424, 1074791425, 1074791456, 1074791457, 1074823168, 1074823169, 
      1074823200, 1074823201, 1074824192, 1074824193, 1074824224, 1074824225, 1107296256, 1107296257, 1107296288, 1107296289, 
      1107297280, 1107297281, 1107297312, 1107297313, 1107329024, 1107329025, 1107329056, 1107329057, 1107330048, 1107330049, 
      1107330080, 1107330081, 1108344832, 1108344833, 1108344864, 1108344865, 1108345856, 1108345857, 1108345888, 1108345889, 
      1108377600, 1108377601, 1108377632, 1108377633, 1108378624, 1108378625, 1108378656, 1108378657 };
  
  private static final long[] INTERLEAVE7_TABLE = new long[] { 
      0L, 1L, 128L, 129L, 16384L, 16385L, 16512L, 16513L, 2097152L, 2097153L, 
      2097280L, 2097281L, 2113536L, 2113537L, 2113664L, 2113665L, 268435456L, 268435457L, 268435584L, 268435585L, 
      268451840L, 268451841L, 268451968L, 268451969L, 270532608L, 270532609L, 270532736L, 270532737L, 270548992L, 270548993L, 
      270549120L, 270549121L, 34359738368L, 34359738369L, 34359738496L, 34359738497L, 34359754752L, 34359754753L, 34359754880L, 34359754881L, 
      34361835520L, 34361835521L, 34361835648L, 34361835649L, 34361851904L, 34361851905L, 34361852032L, 34361852033L, 34628173824L, 34628173825L, 
      34628173952L, 34628173953L, 34628190208L, 34628190209L, 34628190336L, 34628190337L, 34630270976L, 34630270977L, 34630271104L, 34630271105L, 
      34630287360L, 34630287361L, 34630287488L, 34630287489L, 4398046511104L, 4398046511105L, 4398046511232L, 4398046511233L, 4398046527488L, 4398046527489L, 
      4398046527616L, 4398046527617L, 4398048608256L, 4398048608257L, 4398048608384L, 4398048608385L, 4398048624640L, 4398048624641L, 4398048624768L, 4398048624769L, 
      4398314946560L, 4398314946561L, 4398314946688L, 4398314946689L, 4398314962944L, 4398314962945L, 4398314963072L, 4398314963073L, 4398317043712L, 4398317043713L, 
      4398317043840L, 4398317043841L, 4398317060096L, 4398317060097L, 4398317060224L, 4398317060225L, 4432406249472L, 4432406249473L, 4432406249600L, 4432406249601L, 
      4432406265856L, 4432406265857L, 4432406265984L, 4432406265985L, 4432408346624L, 4432408346625L, 4432408346752L, 4432408346753L, 4432408363008L, 4432408363009L, 
      4432408363136L, 4432408363137L, 4432674684928L, 4432674684929L, 4432674685056L, 4432674685057L, 4432674701312L, 4432674701313L, 4432674701440L, 4432674701441L, 
      4432676782080L, 4432676782081L, 4432676782208L, 4432676782209L, 4432676798464L, 4432676798465L, 4432676798592L, 4432676798593L, 562949953421312L, 562949953421313L, 
      562949953421440L, 562949953421441L, 562949953437696L, 562949953437697L, 562949953437824L, 562949953437825L, 562949955518464L, 562949955518465L, 562949955518592L, 562949955518593L, 
      562949955534848L, 562949955534849L, 562949955534976L, 562949955534977L, 562950221856768L, 562950221856769L, 562950221856896L, 562950221856897L, 562950221873152L, 562950221873153L, 
      562950221873280L, 562950221873281L, 562950223953920L, 562950223953921L, 562950223954048L, 562950223954049L, 562950223970304L, 562950223970305L, 562950223970432L, 562950223970433L, 
      562984313159680L, 562984313159681L, 562984313159808L, 562984313159809L, 562984313176064L, 562984313176065L, 562984313176192L, 562984313176193L, 562984315256832L, 562984315256833L, 
      562984315256960L, 562984315256961L, 562984315273216L, 562984315273217L, 562984315273344L, 562984315273345L, 562984581595136L, 562984581595137L, 562984581595264L, 562984581595265L, 
      562984581611520L, 562984581611521L, 562984581611648L, 562984581611649L, 562984583692288L, 562984583692289L, 562984583692416L, 562984583692417L, 562984583708672L, 562984583708673L, 
      562984583708800L, 562984583708801L, 567347999932416L, 567347999932417L, 567347999932544L, 567347999932545L, 567347999948800L, 567347999948801L, 567347999948928L, 567347999948929L, 
      567348002029568L, 567348002029569L, 567348002029696L, 567348002029697L, 567348002045952L, 567348002045953L, 567348002046080L, 567348002046081L, 567348268367872L, 567348268367873L, 
      567348268368000L, 567348268368001L, 567348268384256L, 567348268384257L, 567348268384384L, 567348268384385L, 567348270465024L, 567348270465025L, 567348270465152L, 567348270465153L, 
      567348270481408L, 567348270481409L, 567348270481536L, 567348270481537L, 567382359670784L, 567382359670785L, 567382359670912L, 567382359670913L, 567382359687168L, 567382359687169L, 
      567382359687296L, 567382359687297L, 567382361767936L, 567382361767937L, 567382361768064L, 567382361768065L, 567382361784320L, 567382361784321L, 567382361784448L, 567382361784449L, 
      567382628106240L, 567382628106241L, 567382628106368L, 567382628106369L, 567382628122624L, 567382628122625L, 567382628122752L, 567382628122753L, 567382630203392L, 567382630203393L, 
      567382630203520L, 567382630203521L, 567382630219776L, 567382630219777L, 567382630219904L, 567382630219905L, 72057594037927936L, 72057594037927937L, 72057594037928064L, 72057594037928065L, 
      72057594037944320L, 72057594037944321L, 72057594037944448L, 72057594037944449L, 72057594040025088L, 72057594040025089L, 72057594040025216L, 72057594040025217L, 72057594040041472L, 72057594040041473L, 
      72057594040041600L, 72057594040041601L, 72057594306363392L, 72057594306363393L, 72057594306363520L, 72057594306363521L, 72057594306379776L, 72057594306379777L, 72057594306379904L, 72057594306379905L, 
      72057594308460544L, 72057594308460545L, 72057594308460672L, 72057594308460673L, 72057594308476928L, 72057594308476929L, 72057594308477056L, 72057594308477057L, 72057628397666304L, 72057628397666305L, 
      72057628397666432L, 72057628397666433L, 72057628397682688L, 72057628397682689L, 72057628397682816L, 72057628397682817L, 72057628399763456L, 72057628399763457L, 72057628399763584L, 72057628399763585L, 
      72057628399779840L, 72057628399779841L, 72057628399779968L, 72057628399779969L, 72057628666101760L, 72057628666101761L, 72057628666101888L, 72057628666101889L, 72057628666118144L, 72057628666118145L, 
      72057628666118272L, 72057628666118273L, 72057628668198912L, 72057628668198913L, 72057628668199040L, 72057628668199041L, 72057628668215296L, 72057628668215297L, 72057628668215424L, 72057628668215425L, 
      72061992084439040L, 72061992084439041L, 72061992084439168L, 72061992084439169L, 72061992084455424L, 72061992084455425L, 72061992084455552L, 72061992084455553L, 72061992086536192L, 72061992086536193L, 
      72061992086536320L, 72061992086536321L, 72061992086552576L, 72061992086552577L, 72061992086552704L, 72061992086552705L, 72061992352874496L, 72061992352874497L, 72061992352874624L, 72061992352874625L, 
      72061992352890880L, 72061992352890881L, 72061992352891008L, 72061992352891009L, 72061992354971648L, 72061992354971649L, 72061992354971776L, 72061992354971777L, 72061992354988032L, 72061992354988033L, 
      72061992354988160L, 72061992354988161L, 72062026444177408L, 72062026444177409L, 72062026444177536L, 72062026444177537L, 72062026444193792L, 72062026444193793L, 72062026444193920L, 72062026444193921L, 
      72062026446274560L, 72062026446274561L, 72062026446274688L, 72062026446274689L, 72062026446290944L, 72062026446290945L, 72062026446291072L, 72062026446291073L, 72062026712612864L, 72062026712612865L, 
      72062026712612992L, 72062026712612993L, 72062026712629248L, 72062026712629249L, 72062026712629376L, 72062026712629377L, 72062026714710016L, 72062026714710017L, 72062026714710144L, 72062026714710145L, 
      72062026714726400L, 72062026714726401L, 72062026714726528L, 72062026714726529L, 72620543991349248L, 72620543991349249L, 72620543991349376L, 72620543991349377L, 72620543991365632L, 72620543991365633L, 
      72620543991365760L, 72620543991365761L, 72620543993446400L, 72620543993446401L, 72620543993446528L, 72620543993446529L, 72620543993462784L, 72620543993462785L, 72620543993462912L, 72620543993462913L, 
      72620544259784704L, 72620544259784705L, 72620544259784832L, 72620544259784833L, 72620544259801088L, 72620544259801089L, 72620544259801216L, 72620544259801217L, 72620544261881856L, 72620544261881857L, 
      72620544261881984L, 72620544261881985L, 72620544261898240L, 72620544261898241L, 72620544261898368L, 72620544261898369L, 72620578351087616L, 72620578351087617L, 72620578351087744L, 72620578351087745L, 
      72620578351104000L, 72620578351104001L, 72620578351104128L, 72620578351104129L, 72620578353184768L, 72620578353184769L, 72620578353184896L, 72620578353184897L, 72620578353201152L, 72620578353201153L, 
      72620578353201280L, 72620578353201281L, 72620578619523072L, 72620578619523073L, 72620578619523200L, 72620578619523201L, 72620578619539456L, 72620578619539457L, 72620578619539584L, 72620578619539585L, 
      72620578621620224L, 72620578621620225L, 72620578621620352L, 72620578621620353L, 72620578621636608L, 72620578621636609L, 72620578621636736L, 72620578621636737L, 72624942037860352L, 72624942037860353L, 
      72624942037860480L, 72624942037860481L, 72624942037876736L, 72624942037876737L, 72624942037876864L, 72624942037876865L, 72624942039957504L, 72624942039957505L, 72624942039957632L, 72624942039957633L, 
      72624942039973888L, 72624942039973889L, 72624942039974016L, 72624942039974017L, 72624942306295808L, 72624942306295809L, 72624942306295936L, 72624942306295937L, 72624942306312192L, 72624942306312193L, 
      72624942306312320L, 72624942306312321L, 72624942308392960L, 72624942308392961L, 72624942308393088L, 72624942308393089L, 72624942308409344L, 72624942308409345L, 72624942308409472L, 72624942308409473L, 
      72624976397598720L, 72624976397598721L, 72624976397598848L, 72624976397598849L, 72624976397615104L, 72624976397615105L, 72624976397615232L, 72624976397615233L, 72624976399695872L, 72624976399695873L, 
      72624976399696000L, 72624976399696001L, 72624976399712256L, 72624976399712257L, 72624976399712384L, 72624976399712385L, 72624976666034176L, 72624976666034177L, 72624976666034304L, 72624976666034305L, 
      72624976666050560L, 72624976666050561L, 72624976666050688L, 72624976666050689L, 72624976668131328L, 72624976668131329L, 72624976668131456L, 72624976668131457L, 72624976668147712L, 72624976668147713L, 
      72624976668147840L, 72624976668147841L };
  
  private static final String ZEROES = "0000000000000000000000000000000000000000000000000000000000000000";
  
  static final byte[] bitLengths = new byte[] { 
      0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 
      4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 
      6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 
      7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 
      8, 8, 8, 8, 8, 8 };
  
  private long[] m_ints;
  
  public LongArray(int paramInt) {
    this.m_ints = new long[paramInt];
  }
  
  public LongArray(long[] paramArrayOflong) {
    this.m_ints = paramArrayOflong;
  }
  
  public LongArray(long[] paramArrayOflong, int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == paramArrayOflong.length) {
      this.m_ints = paramArrayOflong;
    } else {
      this.m_ints = new long[paramInt2];
      System.arraycopy(paramArrayOflong, paramInt1, this.m_ints, 0, paramInt2);
    } 
  }
  
  public LongArray(BigInteger paramBigInteger) {
    if (paramBigInteger == null || paramBigInteger.signum() < 0)
      throw new IllegalArgumentException("invalid F2m field value"); 
    if (paramBigInteger.signum() == 0) {
      this.m_ints = new long[] { 0L };
      return;
    } 
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    int i = arrayOfByte.length;
    byte b1 = 0;
    if (arrayOfByte[0] == 0) {
      i--;
      b1 = 1;
    } 
    int j = (i + 7) / 8;
    this.m_ints = new long[j];
    int k = j - 1;
    int m = i % 8 + b1;
    long l = 0L;
    byte b2 = b1;
    if (b1 < m) {
      while (b2 < m) {
        l <<= 8L;
        int n = arrayOfByte[b2] & 0xFF;
        l |= n;
        b2++;
      } 
      this.m_ints[k--] = l;
    } 
    while (k >= 0) {
      l = 0L;
      for (byte b = 0; b < 8; b++) {
        l <<= 8L;
        int n = arrayOfByte[b2++] & 0xFF;
        l |= n;
      } 
      this.m_ints[k] = l;
      k--;
    } 
  }
  
  public boolean isOne() {
    long[] arrayOfLong = this.m_ints;
    if (arrayOfLong[0] != 1L)
      return false; 
    for (byte b = 1; b < arrayOfLong.length; b++) {
      if (arrayOfLong[b] != 0L)
        return false; 
    } 
    return true;
  }
  
  public boolean isZero() {
    long[] arrayOfLong = this.m_ints;
    for (byte b = 0; b < arrayOfLong.length; b++) {
      if (arrayOfLong[b] != 0L)
        return false; 
    } 
    return true;
  }
  
  public int getUsedLength() {
    return getUsedLengthFrom(this.m_ints.length);
  }
  
  public int getUsedLengthFrom(int paramInt) {
    long[] arrayOfLong = this.m_ints;
    paramInt = Math.min(paramInt, arrayOfLong.length);
    if (paramInt < 1)
      return 0; 
    if (arrayOfLong[0] != 0L) {
      while (arrayOfLong[--paramInt] == 0L);
      return paramInt + 1;
    } 
    while (true) {
      if (arrayOfLong[--paramInt] != 0L)
        return paramInt + 1; 
      if (paramInt <= 0)
        return 0; 
    } 
  }
  
  public int degree() {
    int i = this.m_ints.length;
    while (true) {
      if (i == 0)
        return 0; 
      long l = this.m_ints[--i];
      if (l != 0L)
        return (i << 6) + bitLength(l); 
    } 
  }
  
  private int degreeFrom(int paramInt) {
    int i = paramInt + 62 >>> 6;
    while (true) {
      if (i == 0)
        return 0; 
      long l = this.m_ints[--i];
      if (l != 0L)
        return (i << 6) + bitLength(l); 
    } 
  }
  
  private static int bitLength(long paramLong) {
    byte b;
    int k;
    int i = (int)(paramLong >>> 32L);
    if (i == 0) {
      i = (int)paramLong;
      b = 0;
    } else {
      b = 32;
    } 
    int j = i >>> 16;
    if (j == 0) {
      j = i >>> 8;
      k = (j == 0) ? bitLengths[i] : (8 + bitLengths[j]);
    } else {
      int m = j >>> 8;
      k = (m == 0) ? (16 + bitLengths[j]) : (24 + bitLengths[m]);
    } 
    return b + k;
  }
  
  private long[] resizedInts(int paramInt) {
    long[] arrayOfLong = new long[paramInt];
    System.arraycopy(this.m_ints, 0, arrayOfLong, 0, Math.min(this.m_ints.length, paramInt));
    return arrayOfLong;
  }
  
  public BigInteger toBigInteger() {
    int i = getUsedLength();
    if (i == 0)
      return ECConstants.ZERO; 
    long l = this.m_ints[i - 1];
    byte[] arrayOfByte1 = new byte[8];
    byte b = 0;
    boolean bool = false;
    int j;
    for (j = 7; j >= 0; j--) {
      byte b1 = (byte)(int)(l >>> 8 * j);
      if (bool || b1 != 0) {
        bool = true;
        arrayOfByte1[b++] = b1;
      } 
    } 
    j = 8 * (i - 1) + b;
    byte[] arrayOfByte2 = new byte[j];
    int k;
    for (k = 0; k < b; k++)
      arrayOfByte2[k] = arrayOfByte1[k]; 
    for (k = i - 2; k >= 0; k--) {
      long l1 = this.m_ints[k];
      for (byte b1 = 7; b1 >= 0; b1--)
        arrayOfByte2[b++] = (byte)(int)(l1 >>> 8 * b1); 
    } 
    return new BigInteger(1, arrayOfByte2);
  }
  
  private static long shiftUp(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3) {
    int i = 64 - paramInt3;
    long l = 0L;
    for (byte b = 0; b < paramInt2; b++) {
      long l1 = paramArrayOflong[paramInt1 + b];
      paramArrayOflong[paramInt1 + b] = l1 << paramInt3 | l;
      l = l1 >>> i;
    } 
    return l;
  }
  
  private static long shiftUp(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3, int paramInt4) {
    int i = 64 - paramInt4;
    long l = 0L;
    for (byte b = 0; b < paramInt3; b++) {
      long l1 = paramArrayOflong1[paramInt1 + b];
      paramArrayOflong2[paramInt2 + b] = l1 << paramInt4 | l;
      l = l1 >>> i;
    } 
    return l;
  }
  
  public LongArray addOne() {
    if (this.m_ints.length == 0)
      return new LongArray(new long[] { 1L }); 
    int i = Math.max(1, getUsedLength());
    long[] arrayOfLong = resizedInts(i);
    arrayOfLong[0] = arrayOfLong[0] ^ 0x1L;
    return new LongArray(arrayOfLong);
  }
  
  private void addShiftedByBitsSafe(LongArray paramLongArray, int paramInt1, int paramInt2) {
    int i = paramInt1 + 63 >>> 6;
    int j = paramInt2 >>> 6;
    int k = paramInt2 & 0x3F;
    if (k == 0) {
      add(this.m_ints, j, paramLongArray.m_ints, 0, i);
      return;
    } 
    long l = addShiftedUp(this.m_ints, j, paramLongArray.m_ints, 0, i, k);
    if (l != 0L)
      this.m_ints[i + j] = this.m_ints[i + j] ^ l; 
  }
  
  private static long addShiftedUp(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3, int paramInt4) {
    int i = 64 - paramInt4;
    long l = 0L;
    for (byte b = 0; b < paramInt3; b++) {
      long l1 = paramArrayOflong2[paramInt2 + b];
      paramArrayOflong1[paramInt1 + b] = paramArrayOflong1[paramInt1 + b] ^ (l1 << paramInt4 | l);
      l = l1 >>> i;
    } 
    return l;
  }
  
  private static long addShiftedDown(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3, int paramInt4) {
    int i = 64 - paramInt4;
    long l = 0L;
    int j = paramInt3;
    while (--j >= 0) {
      long l1 = paramArrayOflong2[paramInt2 + j];
      paramArrayOflong1[paramInt1 + j] = paramArrayOflong1[paramInt1 + j] ^ (l1 >>> paramInt4 | l);
      l = l1 << i;
    } 
    return l;
  }
  
  public void addShiftedByWords(LongArray paramLongArray, int paramInt) {
    int i = paramLongArray.getUsedLength();
    if (i == 0)
      return; 
    int j = i + paramInt;
    if (j > this.m_ints.length)
      this.m_ints = resizedInts(j); 
    add(this.m_ints, paramInt, paramLongArray.m_ints, 0, i);
  }
  
  private static void add(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt3; b++)
      paramArrayOflong1[paramInt1 + b] = paramArrayOflong1[paramInt1 + b] ^ paramArrayOflong2[paramInt2 + b]; 
  }
  
  private static void add(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, long[] paramArrayOflong3, int paramInt3, int paramInt4) {
    for (byte b = 0; b < paramInt4; b++)
      paramArrayOflong3[paramInt3 + b] = paramArrayOflong1[paramInt1 + b] ^ paramArrayOflong2[paramInt2 + b]; 
  }
  
  private static void addBoth(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, long[] paramArrayOflong3, int paramInt3, int paramInt4) {
    for (byte b = 0; b < paramInt4; b++)
      paramArrayOflong1[paramInt1 + b] = paramArrayOflong1[paramInt1 + b] ^ paramArrayOflong2[paramInt2 + b] ^ paramArrayOflong3[paramInt3 + b]; 
  }
  
  private static void distribute(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    for (byte b = 0; b < paramInt4; b++) {
      long l = paramArrayOflong[paramInt1 + b];
      paramArrayOflong[paramInt2 + b] = paramArrayOflong[paramInt2 + b] ^ l;
      paramArrayOflong[paramInt3 + b] = paramArrayOflong[paramInt3 + b] ^ l;
    } 
  }
  
  public int getLength() {
    return this.m_ints.length;
  }
  
  private static void flipWord(long[] paramArrayOflong, int paramInt1, int paramInt2, long paramLong) {
    int i = paramInt1 + (paramInt2 >>> 6);
    int j = paramInt2 & 0x3F;
    if (j == 0) {
      paramArrayOflong[i] = paramArrayOflong[i] ^ paramLong;
    } else {
      paramArrayOflong[i] = paramArrayOflong[i] ^ paramLong << j;
      paramLong >>>= 64 - j;
      if (paramLong != 0L)
        paramArrayOflong[++i] = paramArrayOflong[++i] ^ paramLong; 
    } 
  }
  
  public boolean testBitZero() {
    return (this.m_ints.length > 0 && (this.m_ints[0] & 0x1L) != 0L);
  }
  
  private static boolean testBit(long[] paramArrayOflong, int paramInt1, int paramInt2) {
    int i = paramInt2 >>> 6;
    int j = paramInt2 & 0x3F;
    long l = 1L << j;
    return ((paramArrayOflong[paramInt1 + i] & l) != 0L);
  }
  
  private static void flipBit(long[] paramArrayOflong, int paramInt1, int paramInt2) {
    int i = paramInt2 >>> 6;
    int j = paramInt2 & 0x3F;
    long l = 1L << j;
    paramArrayOflong[paramInt1 + i] = paramArrayOflong[paramInt1 + i] ^ l;
  }
  
  private static void multiplyWord(long paramLong, long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2) {
    if ((paramLong & 0x1L) != 0L)
      add(paramArrayOflong2, paramInt2, paramArrayOflong1, 0, paramInt1); 
    for (byte b = 1; (paramLong >>>= 1L) != 0L; b++) {
      if ((paramLong & 0x1L) != 0L) {
        long l = addShiftedUp(paramArrayOflong2, paramInt2, paramArrayOflong1, 0, paramInt1, b);
        if (l != 0L)
          paramArrayOflong2[paramInt2 + paramInt1] = paramArrayOflong2[paramInt2 + paramInt1] ^ l; 
      } 
    } 
  }
  
  public LongArray modMultiplyLD(LongArray paramLongArray, int paramInt, int[] paramArrayOfint) {
    int i = degree();
    if (i == 0)
      return this; 
    int j = paramLongArray.degree();
    if (j == 0)
      return paramLongArray; 
    LongArray longArray1 = this;
    LongArray longArray2 = paramLongArray;
    if (i > j) {
      longArray1 = paramLongArray;
      longArray2 = this;
      int i3 = i;
      i = j;
      j = i3;
    } 
    int k = i + 63 >>> 6;
    int m = j + 63 >>> 6;
    int n = i + j + 62 >>> 6;
    if (k == 1) {
      long l = longArray1.m_ints[0];
      if (l == 1L)
        return longArray2; 
      long[] arrayOfLong = new long[n];
      multiplyWord(l, longArray2.m_ints, m, arrayOfLong, 0);
      return reduceResult(arrayOfLong, 0, n, paramInt, paramArrayOfint);
    } 
    int i1 = j + 7 + 63 >>> 6;
    int[] arrayOfInt = new int[16];
    long[] arrayOfLong1 = new long[i1 << 4];
    int i2 = i1;
    arrayOfInt[1] = i2;
    System.arraycopy(longArray2.m_ints, 0, arrayOfLong1, i2, m);
    for (byte b1 = 2; b1 < 16; b1++) {
      arrayOfInt[b1] = i2 += i1;
      if ((b1 & 0x1) == 0) {
        shiftUp(arrayOfLong1, i2 >>> 1, arrayOfLong1, i2, i1, 1);
      } else {
        add(arrayOfLong1, i1, arrayOfLong1, i2 - i1, arrayOfLong1, i2, i1);
      } 
    } 
    long[] arrayOfLong2 = new long[arrayOfLong1.length];
    shiftUp(arrayOfLong1, 0, arrayOfLong2, 0, arrayOfLong1.length, 4);
    long[] arrayOfLong3 = longArray1.m_ints;
    long[] arrayOfLong4 = new long[n];
    byte b2 = 15;
    byte b3;
    for (b3 = 56; b3 >= 0; b3 -= 8) {
      for (byte b = 1; b < k; b += 2) {
        int i3 = (int)(arrayOfLong3[b] >>> b3);
        int i4 = i3 & b2;
        int i5 = i3 >>> 4 & b2;
        addBoth(arrayOfLong4, b - 1, arrayOfLong1, arrayOfInt[i4], arrayOfLong2, arrayOfInt[i5], i1);
      } 
      shiftUp(arrayOfLong4, 0, n, 8);
    } 
    for (b3 = 56; b3 >= 0; b3 -= 8) {
      for (byte b = 0; b < k; b += 2) {
        int i3 = (int)(arrayOfLong3[b] >>> b3);
        int i4 = i3 & b2;
        int i5 = i3 >>> 4 & b2;
        addBoth(arrayOfLong4, b, arrayOfLong1, arrayOfInt[i4], arrayOfLong2, arrayOfInt[i5], i1);
      } 
      if (b3 > 0)
        shiftUp(arrayOfLong4, 0, n, 8); 
    } 
    return reduceResult(arrayOfLong4, 0, n, paramInt, paramArrayOfint);
  }
  
  public LongArray modMultiply(LongArray paramLongArray, int paramInt, int[] paramArrayOfint) {
    int i = degree();
    if (i == 0)
      return this; 
    int j = paramLongArray.degree();
    if (j == 0)
      return paramLongArray; 
    LongArray longArray1 = this;
    LongArray longArray2 = paramLongArray;
    if (i > j) {
      longArray1 = paramLongArray;
      longArray2 = this;
      int i4 = i;
      i = j;
      j = i4;
    } 
    int k = i + 63 >>> 6;
    int m = j + 63 >>> 6;
    int n = i + j + 62 >>> 6;
    if (k == 1) {
      long l = longArray1.m_ints[0];
      if (l == 1L)
        return longArray2; 
      long[] arrayOfLong = new long[n];
      multiplyWord(l, longArray2.m_ints, m, arrayOfLong, 0);
      return reduceResult(arrayOfLong, 0, n, paramInt, paramArrayOfint);
    } 
    int i1 = j + 7 + 63 >>> 6;
    int[] arrayOfInt = new int[16];
    long[] arrayOfLong1 = new long[i1 << 4];
    int i2 = i1;
    arrayOfInt[1] = i2;
    System.arraycopy(longArray2.m_ints, 0, arrayOfLong1, i2, m);
    for (byte b1 = 2; b1 < 16; b1++) {
      arrayOfInt[b1] = i2 += i1;
      if ((b1 & 0x1) == 0) {
        shiftUp(arrayOfLong1, i2 >>> 1, arrayOfLong1, i2, i1, 1);
      } else {
        add(arrayOfLong1, i1, arrayOfLong1, i2 - i1, arrayOfLong1, i2, i1);
      } 
    } 
    long[] arrayOfLong2 = new long[arrayOfLong1.length];
    shiftUp(arrayOfLong1, 0, arrayOfLong2, 0, arrayOfLong1.length, 4);
    long[] arrayOfLong3 = longArray1.m_ints;
    long[] arrayOfLong4 = new long[n << 3];
    byte b2 = 15;
    int i3 = 0;
    while (i3 < k) {
      long l = arrayOfLong3[i3];
      int i4;
      for (i4 = i3;; i4 += n) {
        int i5 = (int)l & b2;
        l >>>= 4L;
        int i6 = (int)l & b2;
        addBoth(arrayOfLong4, i4, arrayOfLong1, arrayOfInt[i5], arrayOfLong2, arrayOfInt[i6], i1);
        l >>>= 4L;
        if (l == 0L) {
          i3++;
          continue;
        } 
      } 
    } 
    i3 = arrayOfLong4.length;
    while ((i3 -= n) != 0)
      addShiftedUp(arrayOfLong4, i3 - n, arrayOfLong4, i3, n, 8); 
    return reduceResult(arrayOfLong4, 0, n, paramInt, paramArrayOfint);
  }
  
  public LongArray modMultiplyAlt(LongArray paramLongArray, int paramInt, int[] paramArrayOfint) {
    int i = degree();
    if (i == 0)
      return this; 
    int j = paramLongArray.degree();
    if (j == 0)
      return paramLongArray; 
    LongArray longArray1 = this;
    LongArray longArray2 = paramLongArray;
    if (i > j) {
      longArray1 = paramLongArray;
      longArray2 = this;
      int i7 = i;
      i = j;
      j = i7;
    } 
    int k = i + 63 >>> 6;
    int m = j + 63 >>> 6;
    int n = i + j + 62 >>> 6;
    if (k == 1) {
      long l = longArray1.m_ints[0];
      if (l == 1L)
        return longArray2; 
      long[] arrayOfLong1 = new long[n];
      multiplyWord(l, longArray2.m_ints, m, arrayOfLong1, 0);
      return reduceResult(arrayOfLong1, 0, n, paramInt, paramArrayOfint);
    } 
    byte b1 = 4;
    byte b2 = 16;
    byte b3 = 64;
    byte b4 = 8;
    byte b5 = (b3 < 64) ? b2 : (b2 - 1);
    int i1 = j + b5 + 63 >>> 6;
    int i2 = i1 * b4;
    int i3 = b1 * b4;
    int[] arrayOfInt = new int[1 << b1];
    int i4 = k;
    arrayOfInt[0] = i4;
    i4 += i2;
    arrayOfInt[1] = i4;
    for (byte b6 = 2; b6 < arrayOfInt.length; b6++) {
      i4 += n;
      arrayOfInt[b6] = i4;
    } 
    i4 += n;
    long[] arrayOfLong = new long[++i4];
    interleave(longArray1.m_ints, 0, arrayOfLong, 0, k, b1);
    int i5 = k;
    System.arraycopy(longArray2.m_ints, 0, arrayOfLong, i5, m);
    int i6;
    for (i6 = 1; i6 < b4; i6++)
      shiftUp(arrayOfLong, k, arrayOfLong, i5 += i1, i1, i6); 
    i5 = (1 << b1) - 1;
    i6 = 0;
    while (true) {
      int i7 = 0;
      while (true) {
        long l = arrayOfLong[i7] >>> i6;
        byte b = 0;
        int i8 = k;
        while (true) {
          int i9 = (int)l & i5;
          if (i9 != 0)
            add(arrayOfLong, i7 + arrayOfInt[i9], arrayOfLong, i8, i1); 
          if (++b == b4) {
            if (++i7 >= k) {
              if ((i6 += i3) >= b3) {
                if (i6 >= 64) {
                  i7 = arrayOfInt.length;
                  while (--i7 > 1) {
                    if ((i7 & 0x1L) == 0L) {
                      addShiftedUp(arrayOfLong, arrayOfInt[i7 >>> 1], arrayOfLong, arrayOfInt[i7], n, b2);
                      continue;
                    } 
                    distribute(arrayOfLong, arrayOfInt[i7], arrayOfInt[i7 - 1], arrayOfInt[1], n);
                  } 
                  return reduceResult(arrayOfLong, arrayOfInt[1], n, paramInt, paramArrayOfint);
                } 
                i6 = 64 - b1;
                i5 &= i5 << b3 - i6;
              } 
              break;
            } 
            continue;
          } 
          i8 += i1;
          l >>>= b1;
        } 
      } 
      shiftUp(arrayOfLong, k, i2, b4);
    } 
  }
  
  public LongArray modReduce(int paramInt, int[] paramArrayOfint) {
    long[] arrayOfLong = Arrays.clone(this.m_ints);
    int i = reduceInPlace(arrayOfLong, 0, arrayOfLong.length, paramInt, paramArrayOfint);
    return new LongArray(arrayOfLong, 0, i);
  }
  
  public LongArray multiply(LongArray paramLongArray, int paramInt, int[] paramArrayOfint) {
    int i = degree();
    if (i == 0)
      return this; 
    int j = paramLongArray.degree();
    if (j == 0)
      return paramLongArray; 
    LongArray longArray1 = this;
    LongArray longArray2 = paramLongArray;
    if (i > j) {
      longArray1 = paramLongArray;
      longArray2 = this;
      int i4 = i;
      i = j;
      j = i4;
    } 
    int k = i + 63 >>> 6;
    int m = j + 63 >>> 6;
    int n = i + j + 62 >>> 6;
    if (k == 1) {
      long l = longArray1.m_ints[0];
      if (l == 1L)
        return longArray2; 
      long[] arrayOfLong = new long[n];
      multiplyWord(l, longArray2.m_ints, m, arrayOfLong, 0);
      return new LongArray(arrayOfLong, 0, n);
    } 
    int i1 = j + 7 + 63 >>> 6;
    int[] arrayOfInt = new int[16];
    long[] arrayOfLong1 = new long[i1 << 4];
    int i2 = i1;
    arrayOfInt[1] = i2;
    System.arraycopy(longArray2.m_ints, 0, arrayOfLong1, i2, m);
    for (byte b1 = 2; b1 < 16; b1++) {
      arrayOfInt[b1] = i2 += i1;
      if ((b1 & 0x1) == 0) {
        shiftUp(arrayOfLong1, i2 >>> 1, arrayOfLong1, i2, i1, 1);
      } else {
        add(arrayOfLong1, i1, arrayOfLong1, i2 - i1, arrayOfLong1, i2, i1);
      } 
    } 
    long[] arrayOfLong2 = new long[arrayOfLong1.length];
    shiftUp(arrayOfLong1, 0, arrayOfLong2, 0, arrayOfLong1.length, 4);
    long[] arrayOfLong3 = longArray1.m_ints;
    long[] arrayOfLong4 = new long[n << 3];
    byte b2 = 15;
    int i3 = 0;
    while (i3 < k) {
      long l = arrayOfLong3[i3];
      int i4;
      for (i4 = i3;; i4 += n) {
        int i5 = (int)l & b2;
        l >>>= 4L;
        int i6 = (int)l & b2;
        addBoth(arrayOfLong4, i4, arrayOfLong1, arrayOfInt[i5], arrayOfLong2, arrayOfInt[i6], i1);
        l >>>= 4L;
        if (l == 0L) {
          i3++;
          continue;
        } 
      } 
    } 
    i3 = arrayOfLong4.length;
    while ((i3 -= n) != 0)
      addShiftedUp(arrayOfLong4, i3 - n, arrayOfLong4, i3, n, 8); 
    return new LongArray(arrayOfLong4, 0, n);
  }
  
  public void reduce(int paramInt, int[] paramArrayOfint) {
    long[] arrayOfLong = this.m_ints;
    int i = reduceInPlace(arrayOfLong, 0, arrayOfLong.length, paramInt, paramArrayOfint);
    if (i < arrayOfLong.length) {
      this.m_ints = new long[i];
      System.arraycopy(arrayOfLong, 0, this.m_ints, 0, i);
    } 
  }
  
  private static LongArray reduceResult(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint) {
    int i = reduceInPlace(paramArrayOflong, paramInt1, paramInt2, paramInt3, paramArrayOfint);
    return new LongArray(paramArrayOflong, paramInt1, i);
  }
  
  private static int reduceInPlace(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint) {
    int i = paramInt3 + 63 >>> 6;
    if (paramInt2 < i)
      return paramInt2; 
    int j = Math.min(paramInt2 << 6, (paramInt3 << 1) - 1);
    int k;
    for (k = (paramInt2 << 6) - j; k >= 64; k -= 64)
      paramInt2--; 
    int m = paramArrayOfint.length;
    int n = paramArrayOfint[m - 1];
    byte b = (m > 1) ? paramArrayOfint[m - 2] : 0;
    int i1 = Math.max(paramInt3, n + 64);
    int i2 = k + Math.min(j - i1, paramInt3 - b) >> 6;
    if (i2 > 1) {
      int i3 = paramInt2 - i2;
      reduceVectorWise(paramArrayOflong, paramInt1, paramInt2, i3, paramInt3, paramArrayOfint);
      while (paramInt2 > i3)
        paramArrayOflong[paramInt1 + --paramInt2] = 0L; 
      j = i3 << 6;
    } 
    if (j > i1) {
      reduceWordWise(paramArrayOflong, paramInt1, paramInt2, i1, paramInt3, paramArrayOfint);
      j = i1;
    } 
    if (j > paramInt3)
      reduceBitWise(paramArrayOflong, paramInt1, j, paramInt3, paramArrayOfint); 
    return i;
  }
  
  private static void reduceBitWise(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint) {
    while (--paramInt2 >= paramInt3) {
      if (testBit(paramArrayOflong, paramInt1, paramInt2))
        reduceBit(paramArrayOflong, paramInt1, paramInt2, paramInt3, paramArrayOfint); 
    } 
  }
  
  private static void reduceBit(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint) {
    flipBit(paramArrayOflong, paramInt1, paramInt2);
    int i = paramInt2 - paramInt3;
    int j = paramArrayOfint.length;
    while (--j >= 0)
      flipBit(paramArrayOflong, paramInt1, paramArrayOfint[j] + i); 
    flipBit(paramArrayOflong, paramInt1, i);
  }
  
  private static void reduceWordWise(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    int i = paramInt3 >>> 6;
    while (--paramInt2 > i) {
      long l1 = paramArrayOflong[paramInt1 + paramInt2];
      if (l1 != 0L) {
        paramArrayOflong[paramInt1 + paramInt2] = 0L;
        reduceWord(paramArrayOflong, paramInt1, paramInt2 << 6, l1, paramInt4, paramArrayOfint);
      } 
    } 
    int j = paramInt3 & 0x3F;
    long l = paramArrayOflong[paramInt1 + i] >>> j;
    if (l != 0L) {
      paramArrayOflong[paramInt1 + i] = paramArrayOflong[paramInt1 + i] ^ l << j;
      reduceWord(paramArrayOflong, paramInt1, paramInt3, l, paramInt4, paramArrayOfint);
    } 
  }
  
  private static void reduceWord(long[] paramArrayOflong, int paramInt1, int paramInt2, long paramLong, int paramInt3, int[] paramArrayOfint) {
    int i = paramInt2 - paramInt3;
    int j = paramArrayOfint.length;
    while (--j >= 0)
      flipWord(paramArrayOflong, paramInt1, i + paramArrayOfint[j], paramLong); 
    flipWord(paramArrayOflong, paramInt1, i, paramLong);
  }
  
  private static void reduceVectorWise(long[] paramArrayOflong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    int i = (paramInt3 << 6) - paramInt4;
    int j = paramArrayOfint.length;
    while (--j >= 0)
      flipVector(paramArrayOflong, paramInt1, paramArrayOflong, paramInt1 + paramInt3, paramInt2 - paramInt3, i + paramArrayOfint[j]); 
    flipVector(paramArrayOflong, paramInt1, paramArrayOflong, paramInt1 + paramInt3, paramInt2 - paramInt3, i);
  }
  
  private static void flipVector(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3, int paramInt4) {
    paramInt1 += paramInt4 >>> 6;
    paramInt4 &= 0x3F;
    if (paramInt4 == 0) {
      add(paramArrayOflong1, paramInt1, paramArrayOflong2, paramInt2, paramInt3);
    } else {
      long l = addShiftedDown(paramArrayOflong1, paramInt1 + 1, paramArrayOflong2, paramInt2, paramInt3, 64 - paramInt4);
      paramArrayOflong1[paramInt1] = paramArrayOflong1[paramInt1] ^ l;
    } 
  }
  
  public LongArray modSquare(int paramInt, int[] paramArrayOfint) {
    int i = getUsedLength();
    if (i == 0)
      return this; 
    int j = i << 1;
    long[] arrayOfLong = new long[j];
    byte b = 0;
    while (b < j) {
      long l = this.m_ints[b >>> 1];
      arrayOfLong[b++] = interleave2_32to64((int)l);
      arrayOfLong[b++] = interleave2_32to64((int)(l >>> 32L));
    } 
    return new LongArray(arrayOfLong, 0, reduceInPlace(arrayOfLong, 0, arrayOfLong.length, paramInt, paramArrayOfint));
  }
  
  public LongArray modSquareN(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    int i = getUsedLength();
    if (i == 0)
      return this; 
    int j = paramInt2 + 63 >>> 6;
    long[] arrayOfLong = new long[j << 1];
    System.arraycopy(this.m_ints, 0, arrayOfLong, 0, i);
    while (--paramInt1 >= 0) {
      squareInPlace(arrayOfLong, i, paramInt2, paramArrayOfint);
      i = reduceInPlace(arrayOfLong, 0, arrayOfLong.length, paramInt2, paramArrayOfint);
    } 
    return new LongArray(arrayOfLong, 0, i);
  }
  
  public LongArray square(int paramInt, int[] paramArrayOfint) {
    int i = getUsedLength();
    if (i == 0)
      return this; 
    int j = i << 1;
    long[] arrayOfLong = new long[j];
    byte b = 0;
    while (b < j) {
      long l = this.m_ints[b >>> 1];
      arrayOfLong[b++] = interleave2_32to64((int)l);
      arrayOfLong[b++] = interleave2_32to64((int)(l >>> 32L));
    } 
    return new LongArray(arrayOfLong, 0, arrayOfLong.length);
  }
  
  private static void squareInPlace(long[] paramArrayOflong, int paramInt1, int paramInt2, int[] paramArrayOfint) {
    int i = paramInt1 << 1;
    while (--paramInt1 >= 0) {
      long l = paramArrayOflong[paramInt1];
      paramArrayOflong[--i] = interleave2_32to64((int)(l >>> 32L));
      paramArrayOflong[--i] = interleave2_32to64((int)l);
    } 
  }
  
  private static void interleave(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3, int paramInt4) {
    switch (paramInt4) {
      case 3:
        interleave3(paramArrayOflong1, paramInt1, paramArrayOflong2, paramInt2, paramInt3);
        return;
      case 5:
        interleave5(paramArrayOflong1, paramInt1, paramArrayOflong2, paramInt2, paramInt3);
        return;
      case 7:
        interleave7(paramArrayOflong1, paramInt1, paramArrayOflong2, paramInt2, paramInt3);
        return;
    } 
    interleave2_n(paramArrayOflong1, paramInt1, paramArrayOflong2, paramInt2, paramInt3, bitLengths[paramInt4] - 1);
  }
  
  private static void interleave3(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt3; b++)
      paramArrayOflong2[paramInt2 + b] = interleave3(paramArrayOflong1[paramInt1 + b]); 
  }
  
  private static long interleave3(long paramLong) {
    long l = paramLong & Long.MIN_VALUE;
    return l | interleave3_21to63((int)paramLong & 0x1FFFFF) | interleave3_21to63((int)(paramLong >>> 21L) & 0x1FFFFF) << 1L | interleave3_21to63((int)(paramLong >>> 42L) & 0x1FFFFF) << 2L;
  }
  
  private static long interleave3_21to63(int paramInt) {
    int i = INTERLEAVE3_TABLE[paramInt & 0x7F];
    int j = INTERLEAVE3_TABLE[paramInt >>> 7 & 0x7F];
    int k = INTERLEAVE3_TABLE[paramInt >>> 14];
    return (k & 0xFFFFFFFFL) << 42L | (j & 0xFFFFFFFFL) << 21L | i & 0xFFFFFFFFL;
  }
  
  private static void interleave5(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt3; b++)
      paramArrayOflong2[paramInt2 + b] = interleave5(paramArrayOflong1[paramInt1 + b]); 
  }
  
  private static long interleave5(long paramLong) {
    return interleave3_13to65((int)paramLong & 0x1FFF) | interleave3_13to65((int)(paramLong >>> 13L) & 0x1FFF) << 1L | interleave3_13to65((int)(paramLong >>> 26L) & 0x1FFF) << 2L | interleave3_13to65((int)(paramLong >>> 39L) & 0x1FFF) << 3L | interleave3_13to65((int)(paramLong >>> 52L) & 0x1FFF) << 4L;
  }
  
  private static long interleave3_13to65(int paramInt) {
    int i = INTERLEAVE5_TABLE[paramInt & 0x7F];
    int j = INTERLEAVE5_TABLE[paramInt >>> 7];
    return (j & 0xFFFFFFFFL) << 35L | i & 0xFFFFFFFFL;
  }
  
  private static void interleave7(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt3; b++)
      paramArrayOflong2[paramInt2 + b] = interleave7(paramArrayOflong1[paramInt1 + b]); 
  }
  
  private static long interleave7(long paramLong) {
    long l = paramLong & Long.MIN_VALUE;
    return l | INTERLEAVE7_TABLE[(int)paramLong & 0x1FF] | INTERLEAVE7_TABLE[(int)(paramLong >>> 9L) & 0x1FF] << 1L | INTERLEAVE7_TABLE[(int)(paramLong >>> 18L) & 0x1FF] << 2L | INTERLEAVE7_TABLE[(int)(paramLong >>> 27L) & 0x1FF] << 3L | INTERLEAVE7_TABLE[(int)(paramLong >>> 36L) & 0x1FF] << 4L | INTERLEAVE7_TABLE[(int)(paramLong >>> 45L) & 0x1FF] << 5L | INTERLEAVE7_TABLE[(int)(paramLong >>> 54L) & 0x1FF] << 6L;
  }
  
  private static void interleave2_n(long[] paramArrayOflong1, int paramInt1, long[] paramArrayOflong2, int paramInt2, int paramInt3, int paramInt4) {
    for (byte b = 0; b < paramInt3; b++)
      paramArrayOflong2[paramInt2 + b] = interleave2_n(paramArrayOflong1[paramInt1 + b], paramInt4); 
  }
  
  private static long interleave2_n(long paramLong, int paramInt) {
    while (paramInt > 1) {
      paramInt -= 2;
      paramLong = interleave4_16to64((int)paramLong & 0xFFFF) | interleave4_16to64((int)(paramLong >>> 16L) & 0xFFFF) << 1L | interleave4_16to64((int)(paramLong >>> 32L) & 0xFFFF) << 2L | interleave4_16to64((int)(paramLong >>> 48L) & 0xFFFF) << 3L;
    } 
    if (paramInt > 0)
      paramLong = interleave2_32to64((int)paramLong) | interleave2_32to64((int)(paramLong >>> 32L)) << 1L; 
    return paramLong;
  }
  
  private static long interleave4_16to64(int paramInt) {
    int i = INTERLEAVE4_TABLE[paramInt & 0xFF];
    int j = INTERLEAVE4_TABLE[paramInt >>> 8];
    return (j & 0xFFFFFFFFL) << 32L | i & 0xFFFFFFFFL;
  }
  
  private static long interleave2_32to64(int paramInt) {
    int i = INTERLEAVE2_TABLE[paramInt & 0xFF] | INTERLEAVE2_TABLE[paramInt >>> 8 & 0xFF] << 16;
    int j = INTERLEAVE2_TABLE[paramInt >>> 16 & 0xFF] | INTERLEAVE2_TABLE[paramInt >>> 24] << 16;
    return (j & 0xFFFFFFFFL) << 32L | i & 0xFFFFFFFFL;
  }
  
  public LongArray modInverse(int paramInt, int[] paramArrayOfint) {
    int i = degree();
    if (i == 0)
      throw new IllegalStateException(); 
    if (i == 1)
      return this; 
    LongArray longArray1 = (LongArray)clone();
    int j = paramInt + 63 >>> 6;
    LongArray longArray2 = new LongArray(j);
    reduceBit(longArray2.m_ints, 0, paramInt, paramInt, paramArrayOfint);
    LongArray longArray3 = new LongArray(j);
    longArray3.m_ints[0] = 1L;
    LongArray longArray4 = new LongArray(j);
    int[] arrayOfInt1 = { i, paramInt + 1 };
    LongArray[] arrayOfLongArray1 = { longArray1, longArray2 };
    int[] arrayOfInt2 = { 1, 0 };
    LongArray[] arrayOfLongArray2 = { longArray3, longArray4 };
    int k = 1;
    int m = arrayOfInt1[k];
    int n = arrayOfInt2[k];
    int i1 = m - arrayOfInt1[1 - k];
    while (true) {
      if (i1 < 0) {
        i1 = -i1;
        arrayOfInt1[k] = m;
        arrayOfInt2[k] = n;
        k = 1 - k;
        m = arrayOfInt1[k];
        n = arrayOfInt2[k];
      } 
      arrayOfLongArray1[k].addShiftedByBitsSafe(arrayOfLongArray1[1 - k], arrayOfInt1[1 - k], i1);
      int i2 = arrayOfLongArray1[k].degreeFrom(m);
      if (i2 == 0)
        return arrayOfLongArray2[1 - k]; 
      int i3 = arrayOfInt2[1 - k];
      arrayOfLongArray2[k].addShiftedByBitsSafe(arrayOfLongArray2[1 - k], i3, i1);
      i3 += i1;
      if (i3 > n) {
        n = i3;
      } else if (i3 == n) {
        n = arrayOfLongArray2[k].degreeFrom(n);
      } 
      i1 += i2 - m;
      m = i2;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof LongArray))
      return false; 
    LongArray longArray = (LongArray)paramObject;
    int i = getUsedLength();
    if (longArray.getUsedLength() != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (this.m_ints[b] != longArray.m_ints[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = getUsedLength();
    int j = 1;
    for (byte b = 0; b < i; b++) {
      long l = this.m_ints[b];
      j *= 31;
      j ^= (int)l;
      j *= 31;
      j ^= (int)(l >>> 32L);
    } 
    return j;
  }
  
  public Object clone() {
    return new LongArray(Arrays.clone(this.m_ints));
  }
  
  public String toString() {
    int i = getUsedLength();
    if (i == 0)
      return "0"; 
    StringBuffer stringBuffer = new StringBuffer(Long.toBinaryString(this.m_ints[--i]));
    while (--i >= 0) {
      String str = Long.toBinaryString(this.m_ints[i]);
      int j = str.length();
      if (j < 64)
        stringBuffer.append("0000000000000000000000000000000000000000000000000000000000000000".substring(j)); 
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
}
