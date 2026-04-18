package com.hoabui.virtualbody3d.data.local.db.seed

import com.hoabui.virtualbody3d.data.model.BodyCompositionSectionDto
import com.hoabui.virtualbody3d.data.model.BodyScanResultDto
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.data.model.MetabolicSectionDto
import com.hoabui.virtualbody3d.data.model.MetricWithRangeDto
import com.hoabui.virtualbody3d.data.model.MuscleFatAnalysisSectionDto
import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import com.hoabui.virtualbody3d.data.model.ObesityAnalysisSectionDto
import com.hoabui.virtualbody3d.data.model.ProgressSnapshotDto
import com.hoabui.virtualbody3d.data.model.SegmentalAnalysisSectionDto

/**
 * Pure seed DTO builders used by [DatabaseSeeder] (migration + fresh install).
 * No [android.R] ids — image uses [ExerciseDto.localImageName] only.
 */
object CatalogSeedData {

    fun exerciseRowsForSeed(): List<ExerciseDto> = listOf(
        ExerciseDto(
            id = "1",
            name = "Bench Press",
            imageResId = null,
            localImageName = "bench_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Nằm trên ghế, mắt thẳng dưới thanh đòn.\n2. Hạ tạ xuống giữa ngực, cùi chỏ tạo góc 45-75 độ.\n3. Đẩy tạ lên và thở ra mạnh.",
            equipment = "Barbell",
            safetyNotes = "Giữ bả vai ép sát xuống ghế trong suốt quá trình tập.",
            lastWeightKg = 80.0
        ),
        ExerciseDto(
            id = "2",
            name = "Incline Dumbbell Press",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Chỉnh ghế dốc 30-45 độ.\n2. Hạ tạ xuống ngang ngực trên, giữ cổ tay thẳng.\n3. Ép ngực đẩy tạ lên cao nhưng không khóa khớp cùi chỏ.",
            equipment = "Dumbbell",
            safetyNotes = "Tránh để cùi chỏ mở quá rộng gây áp lực lên khớp vai.",
            lastWeightKg = 30.0
        ),
        ExerciseDto(
            id = "3",
            name = "Lat Pulldown",
            imageResId = null,
            localImageName = "lat_pulldown",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Ngồi thẳng, tay cầm thanh xà rộng hơn vai.\n2. Kéo thanh xà xuống sát ngực trên, hướng cùi chỏ ra sau.\n3. Thả tạ lên chậm và cảm nhận cơ xô dãn ra.",
            equipment = "Cable",
            safetyNotes = "Dùng cơ lưng để kéo, tránh dùng lực quán tính từ việc ngả người.",
            lastWeightKg = 50.0
        ),
        ExerciseDto(
            id = "4",
            name = "Seated Cable Row",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Ngồi thẳng lưng, chân đặt vững trên bàn đạp.\n2. Kéo tay cầm về phía bụng dưới, ép chặt xương bả vai.\n3. Giữ lưng cố định, không ngả người quá sâu.",
            equipment = "Cable",
            safetyNotes = "Luôn giữ ngực cao và lưng thẳng.",
            lastWeightKg = 55.0
        ),
        ExerciseDto(
            id = "5",
            name = "Overhead Press",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Strength",
            description = "1. Đứng thẳng, tay cầm tạ rộng bằng vai.\n2. Đẩy tạ thẳng lên trên đầu cho đến khi tay thẳng.\n3. Gồng core và mông để giữ cơ thể ổn định.",
            equipment = "Barbell",
            safetyNotes = "Không để lưng dưới bị võng khi đẩy tạ lên cao.",
            lastWeightKg = 45.0
        ),
        ExerciseDto(
            id = "6",
            name = "Lateral Raise",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Strength",
            description = "1. Đứng thẳng, tay cầm tạ đơn hai bên hông.\n2. Nâng tạ sang hai bên cho đến khi ngang vai.\n3. Hạ tạ xuống chậm để tối ưu áp lực lên cơ vai giữa.",
            equipment = "Dumbbell",
            safetyNotes = "Kiểm soát nhịp hạ tạ, tránh vung vẩy quá mạnh.",
            lastWeightKg = 12.0
        ),
        ExerciseDto(
            id = "7",
            name = "Barbell Curl",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Arms",
            category = "Strength",
            description = "1. Đứng thẳng, lòng bàn tay hướng về phía trước.\n2. Cuộn tạ lên phía vai, giữ cùi chỏ cố định sát sườn.\n3. Thả tạ xuống chậm, không để tạ rơi tự do.",
            equipment = "Barbell",
            safetyNotes = "Không đung đưa thân người để lấy đà.",
            lastWeightKg = 25.0
        ),
        ExerciseDto(
            id = "8",
            name = "Triceps Pushdown",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Arms",
            category = "Strength",
            description = "1. Đứng hơi nghiêng người, tay cầm thanh xà hoặc dây cáp.\n2. Đẩy cáp xuống cho đến khi tay thẳng hoàn toàn.\n3. Giữ cùi chỏ cố định, chỉ di chuyển cẳng tay.",
            equipment = "Cable",
            safetyNotes = "Giữ cùi chỏ sát thân mình trong suốt hiệp tập.",
            lastWeightKg = 30.0
        ),
        ExerciseDto(
            id = "9",
            name = "Plank",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Core",
            category = "Mobility",
            description = "1. Chống cùi chỏ xuống sàn, cơ thể thẳng từ đầu đến gót chân.\n2. Siết chặt cơ bụng và mông.\n3. Duy trì nhịp thở đều đặn trong suốt thời gian giữ.",
            equipment = "Bodyweight",
            safetyNotes = "Tránh để hông bị võng xuống sàn gây đau lưng dưới.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "10",
            name = "Russian Twist",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Core",
            category = "Stretching",
            description = "1. Ngồi hơi ngả người, chân nhấc khỏi sàn.\n2. Xoay thân người sang hai bên, tay chạm sàn hoặc cầm tạ.\n3. Tập trung vào sự co bóp của cơ bụng chéo.",
            equipment = "Bodyweight",
            safetyNotes = "Di chuyển có kiểm soát, không xoay quá đà gây ảnh hưởng cột sống."
        ),
        ExerciseDto(
            id = "11",
            name = "Squat",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Đứng chân rộng bằng vai, mũi chân hướng nhẹ ra ngoài.\n2. Hạ hông xuống như đang ngồi vào ghế, giữ lưng thẳng.\n3. Đạp mạnh gót chân để đứng dậy.",
            equipment = "Barbell",
            safetyNotes = "Luôn để đầu gối hướng theo hướng mũi chân.",
            lastWeightKg = 100.0
        ),
        ExerciseDto(
            id = "12",
            name = "Romanian Deadlift",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Hạ tạ xuống dọc theo chân bằng cách đẩy hông ra sau.\n2. Giữ lưng thẳng, hạ đến khi cảm thấy cơ đùi sau căng mạnh.\n3. Kéo hông về phía trước để đứng dậy.",
            equipment = "Barbell",
            safetyNotes = "Tuyệt đối không để lưng bị cong khi hạ tạ.",
            lastWeightKg = 90.0
        ),
        ExerciseDto(
            id = "13",
            name = "Jumping Jacks",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Cardio",
            description = "1. Đứng thẳng, hai tay thả lỏng.\n2. Nhảy bật chân sang hai bên đồng thời vỗ tay trên đầu.\n3. Nhảy thu chân về vị trí ban đầu.",
            equipment = "Bodyweight",
            safetyNotes = "Tiếp đất nhẹ nhàng bằng mũi chân để bảo vệ khớp gối."
        ),
        ExerciseDto(
            id = "14",
            name = "World's Greatest Stretch",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Mobility",
            description = "1. Bước một chân dài lên phía trước (Lunge).\n2. Đặt tay đối diện xuống sàn, tay còn lại xoay hướng lên trời.\n3. Giữ 2-3 giây rồi thực hiện đổi bên.",
            equipment = "Bodyweight",
            safetyNotes = "Giữ đầu gối chân trước thẳng hàng với cổ chân."
        ),
        ExerciseDto(
            id = "15",
            name = "Deadlift",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Đứng chân rộng bằng vai, tay nắm thanh đòn.\n2. Đẩy hông ra sau, giữ lưng thẳng khi kéo tạ lên.\n3. Khóa hông ở vị trí đứng thẳng, sau đó hạ tạ có kiểm soát.",
            equipment = "Barbell",
            safetyNotes = "Giữ cột sống trung lập, không gù lưng.",
            lastWeightKg = 120.0
        ),
        ExerciseDto(
            id = "16",
            name = "Leg Press",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Đặt chân lên bàn đạp, lưng tựa chắc vào ghế.\n2. Đẩy bàn đạp ra cho đến khi chân gần thẳng.\n3. Hạ tạ xuống chậm cho đến khi gối tạo góc 90 độ.",
            equipment = "Machine",
            safetyNotes = "Không khóa khớp gối ở cuối động tác.",
            lastWeightKg = 160.0
        ),
        ExerciseDto(
            id = "17",
            name = "Walking Lunges",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Bước dài về phía trước, hạ gối sau gần chạm sàn.\n2. Giữ thân người thẳng và đẩy bằng gót chân trước.\n3. Bước tiếp tục với chân còn lại.",
            equipment = "Dumbbell",
            safetyNotes = "Giữ đầu gối trước không vượt quá mũi chân.",
            lastWeightKg = 20.0
        ),
        ExerciseDto(
            id = "18",
            name = "Leg Extension",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Ngồi vào máy, đặt cổ chân dưới đệm.\n2. Duỗi chân lên cho đến khi đùi trước co căng.\n3. Hạ tạ xuống chậm và có kiểm soát.",
            equipment = "Machine",
            safetyNotes = "Không đá tạ mạnh gây áp lực lên khớp gối.",
            lastWeightKg = 45.0
        ),
        ExerciseDto(
            id = "19",
            name = "Leg Curl",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Nằm trên máy, đặt gót chân dưới đệm.\n2. Co gối kéo đệm về phía mông.\n3. Hạ tạ xuống chậm để cảm nhận cơ đùi sau.",
            equipment = "Machine",
            safetyNotes = "Giữ hông áp sát ghế, tránh cong lưng.",
            lastWeightKg = 40.0
        ),
        ExerciseDto(
            id = "20",
            name = "Standing Calf Raise",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Đứng trên bục, gót chân rơi thấp xuống.\n2. Nhón gót lên cao hết mức.\n3. Hạ gót xuống chậm để kéo giãn cơ bắp chân.",
            equipment = "Machine",
            safetyNotes = "Giữ chuyển động chậm và kiểm soát.",
            lastWeightKg = 60.0
        ),
        ExerciseDto(
            id = "21",
            name = "Hip Thrust",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Tựa lưng trên ghế, thanh tạ đặt ngang hông.\n2. Đẩy hông lên đến khi cơ thể thẳng hàng.\n3. Siết mông ở điểm cao nhất rồi hạ xuống chậm.",
            equipment = "Barbell",
            safetyNotes = "Không ưỡn lưng quá mức ở điểm cuối.",
            lastWeightKg = 90.0
        ),
        ExerciseDto(
            id = "22",
            name = "Glute Bridge",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Strength",
            description = "1. Nằm ngửa, gập gối và đặt chân trên sàn.\n2. Đẩy hông lên cao, siết mông.\n3. Hạ hông xuống chậm và lặp lại.",
            equipment = "Bodyweight",
            safetyNotes = "Giữ lưng dưới trung lập, không ưỡn quá sâu."
        ),
        ExerciseDto(
            id = "23",
            name = "Pull-up",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Treo người trên xà, tay rộng hơn vai.\n2. Kéo người lên cho đến khi cằm vượt qua xà.\n3. Hạ xuống chậm với kiểm soát.",
            equipment = "Bodyweight",
            safetyNotes = "Tránh đung đưa người để lấy đà."
        ),
        ExerciseDto(
            id = "24",
            name = "Chin-up",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Treo người trên xà, lòng bàn tay hướng vào trong.\n2. Kéo lên cho đến khi cằm qua xà.\n3. Hạ xuống chậm, giữ vai ổn định.",
            equipment = "Bodyweight",
            safetyNotes = "Giữ cổ tay thẳng, tránh căng quá mức."
        ),
        ExerciseDto(
            id = "25",
            name = "Bent-Over Row",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Gập hông, giữ lưng thẳng và ngực mở.\n2. Kéo thanh tạ về bụng, ép bả vai lại.\n3. Hạ tạ xuống chậm để cảm nhận cơ lưng.",
            equipment = "Barbell",
            safetyNotes = "Giữ lưng trung lập, không cong lưng dưới.",
            lastWeightKg = 70.0
        ),
        ExerciseDto(
            id = "26",
            name = "T-Bar Row",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Back",
            category = "Strength",
            description = "1. Đứng gập người, tay cầm tay nắm máy.\n2. Kéo tạ về phía ngực dưới, siết lưng.\n3. Hạ tạ xuống chậm và kiểm soát.",
            equipment = "Machine",
            safetyNotes = "Giữ ngực mở và không nhún người.",
            lastWeightKg = 65.0
        ),
        ExerciseDto(
            id = "27",
            name = "Face Pull",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Strength",
            description = "1. Cầm dây cáp ngang mặt, khuỷu tay cao.\n2. Kéo dây về phía mặt, siết cơ vai sau.\n3. Trả dây về chậm với kiểm soát.",
            equipment = "Cable",
            safetyNotes = "Giữ cổ tay thẳng, không nhún vai."
        ),
        ExerciseDto(
            id = "28",
            name = "Dumbbell Chest Fly",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Nằm trên ghế, tay cầm tạ mở rộng hai bên.\n2. Hạ tạ xuống cho đến khi ngực căng.\n3. Ép ngực đưa tạ trở lại vị trí ban đầu.",
            equipment = "Dumbbell",
            safetyNotes = "Giữ khuỷu tay hơi cong để bảo vệ khớp vai.",
            lastWeightKg = 16.0
        ),
        ExerciseDto(
            id = "29",
            name = "Push-up",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Chống tay rộng hơn vai, thân người thẳng.\n2. Hạ ngực xuống gần sàn.\n3. Đẩy người lên và thở ra mạnh.",
            equipment = "Bodyweight",
            safetyNotes = "Giữ core siết chặt để tránh võng lưng."
        ),
        ExerciseDto(
            id = "30",
            name = "Dips",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Chống tay trên thanh song song, thân người thẳng.\n2. Hạ người xuống đến khi khuỷu tay 90 độ.\n3. Đẩy người lên, siết cơ ngực và tay sau.",
            equipment = "Bodyweight",
            safetyNotes = "Không hạ quá sâu gây áp lực lên vai."
        ),
        ExerciseDto(
            id = "31",
            name = "Cable Chest Press",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Đứng giữa hai ròng rọc, tay cầm tay nắm.\n2. Đẩy tay ra trước và ép cơ ngực.\n3. Thu tay về chậm, giữ vai ổn định.",
            equipment = "Cable",
            safetyNotes = "Giữ thân người ổn định, không đổ người quá nhiều.",
            lastWeightKg = 25.0
        ),
        ExerciseDto(
            id = "32",
            name = "Pec Deck",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Chest",
            category = "Strength",
            description = "1. Ngồi vào máy, tay đặt lên đệm.\n2. Ép tay vào nhau cho đến khi ngực siết chặt.\n3. Mở tay ra chậm để kéo giãn cơ ngực.",
            equipment = "Machine",
            safetyNotes = "Không khóa khớp khuỷu tay ở cuối động tác.",
            lastWeightKg = 40.0
        ),
        ExerciseDto(
            id = "33",
            name = "Arnold Press",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Strength",
            description = "1. Ngồi thẳng, tạ trước ngực, lòng bàn tay hướng vào.\n2. Xoay tạ và đẩy lên trên đầu.\n3. Hạ tạ xuống chậm, xoay về vị trí ban đầu.",
            equipment = "Dumbbell",
            safetyNotes = "Giữ cổ tay thẳng để tránh chấn thương.",
            lastWeightKg = 18.0
        ),
        ExerciseDto(
            id = "34",
            name = "Front Raise",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Strength",
            description = "1. Đứng thẳng, cầm tạ trước đùi.\n2. Nâng tạ lên ngang vai, giữ khuỷu tay hơi cong.\n3. Hạ tạ xuống chậm và kiểm soát.",
            equipment = "Dumbbell",
            safetyNotes = "Tránh vung tạ bằng quán tính.",
            lastWeightKg = 10.0
        ),
        ExerciseDto(
            id = "35",
            name = "Rear Delt Fly",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Strength",
            description = "1. Gập người, tay cầm tạ thả xuống.\n2. Mở tay sang hai bên cho đến khi ngang vai.\n3. Hạ tạ xuống chậm, siết cơ vai sau.",
            equipment = "Dumbbell",
            safetyNotes = "Giữ lưng thẳng và không nhún vai.",
            lastWeightKg = 8.0
        ),
        ExerciseDto(
            id = "36",
            name = "Hammer Curl",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Arms",
            category = "Strength",
            description = "1. Đứng thẳng, tay cầm tạ song song.\n2. Cuộn tạ lên phía vai, giữ khuỷu tay cố định.\n3. Hạ tạ xuống chậm và kiểm soát.",
            equipment = "Dumbbell",
            safetyNotes = "Không đung đưa thân người.",
            lastWeightKg = 16.0
        ),
        ExerciseDto(
            id = "37",
            name = "Preacher Curl",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Arms",
            category = "Strength",
            description = "1. Đặt tay lên ghế preacher, cầm thanh tạ.\n2. Cuộn tạ lên chậm cho đến khi bắp tay siết.\n3. Hạ tạ xuống chậm đến khi tay gần thẳng.",
            equipment = "Barbell",
            safetyNotes = "Không duỗi tay quá mức ở cuối động tác.",
            lastWeightKg = 20.0
        ),
        ExerciseDto(
            id = "38",
            name = "Overhead Triceps Extension",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Arms",
            category = "Strength",
            description = "1. Cầm tạ qua đầu, khuỷu tay hướng lên.\n2. Duỗi tay lên cao cho đến khi thẳng.\n3. Hạ tạ xuống chậm, giữ khuỷu tay cố định.",
            equipment = "Dumbbell",
            safetyNotes = "Tránh mở khuỷu tay quá rộng.",
            lastWeightKg = 14.0
        ),
        ExerciseDto(
            id = "39",
            name = "Skull Crushers",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Arms",
            category = "Strength",
            description = "1. Nằm trên ghế, giữ thanh tạ thẳng trên ngực.\n2. Gập khuỷu tay, hạ tạ xuống gần trán.\n3. Duỗi tay lên lại, siết cơ tay sau.",
            equipment = "Barbell",
            safetyNotes = "Không hạ tạ quá thấp để tránh chấn thương.",
            lastWeightKg = 25.0
        ),
        ExerciseDto(
            id = "40",
            name = "Bicycle Crunch",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Core",
            category = "Strength",
            description = "1. Nằm ngửa, nâng vai khỏi sàn.\n2. Đưa khuỷu tay chạm gối đối diện.\n3. Luân phiên đổi bên với nhịp thở đều.",
            equipment = "Bodyweight",
            safetyNotes = "Không kéo cổ bằng tay.",
        ),
        ExerciseDto(
            id = "41",
            name = "Hanging Leg Raise",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Core",
            category = "Strength",
            description = "1. Treo người trên xà, chân thả thẳng.\n2. Nâng chân lên cao đến khi hông gập.\n3. Hạ chân xuống chậm với kiểm soát.",
            equipment = "Bodyweight",
            safetyNotes = "Tránh đung đưa người quá nhiều."
        ),
        ExerciseDto(
            id = "42",
            name = "Mountain Climbers",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Core",
            category = "Cardio",
            description = "1. Vào tư thế plank, tay chống thẳng.\n2. Kéo gối về phía ngực luân phiên nhanh.\n3. Giữ thân người ổn định trong suốt bài tập.",
            equipment = "Bodyweight",
            safetyNotes = "Không để hông bị võng xuống.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "43",
            name = "Burpees",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Full Body",
            category = "Cardio",
            description = "1. Ngồi xổm, đặt tay xuống sàn.\n2. Bật chân ra sau, hạ ngực xuống.\n3. Bật chân về, nhảy lên cao và vỗ tay.",
            equipment = "Bodyweight",
            safetyNotes = "Giữ nhịp thở đều và tránh tiếp đất mạnh.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "44",
            name = "Treadmill Run",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Cardio",
            description = "1. Khởi động với tốc độ chậm.\n2. Tăng tốc đến mức mục tiêu và giữ nhịp.\n3. Hạ tốc độ dần để thả lỏng.",
            equipment = "Machine",
            safetyNotes = "Giữ tư thế thẳng, tránh nắm tay vịn quá chặt.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "45",
            name = "Rowing Machine",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Full Body",
            category = "Cardio",
            description = "1. Đạp chân mạnh, kéo tay về bụng dưới.\n2. Ngả thân người nhẹ về sau khi kéo.\n3. Trượt ghế về trước và lặp lại nhịp.",
            equipment = "Machine",
            safetyNotes = "Giữ lưng thẳng trong toàn bộ chuyển động.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "46",
            name = "Jump Rope",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Cardio",
            description = "1. Cầm dây, khuỷu tay gần thân người.\n2. Xoay dây bằng cổ tay và bật nhẹ bằng mũi chân.\n3. Giữ nhịp đều và thở ổn định.",
            equipment = "Bodyweight",
            safetyNotes = "Tiếp đất nhẹ để bảo vệ khớp gối.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "47",
            name = "Stationary Bike",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Legs",
            category = "Cardio",
            description = "1. Điều chỉnh yên phù hợp, lưng thẳng.\n2. Đạp đều theo nhịp mục tiêu.\n3. Giảm tốc dần để kết thúc buổi tập.",
            equipment = "Machine",
            safetyNotes = "Giữ gối thẳng hàng với mũi chân.",
            measurementMode = "duration",
        ),
        ExerciseDto(
            id = "48",
            name = "Child's Pose",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Full Body",
            category = "Stretching",
            description = "1. Quỳ gối, ngồi lên gót chân.\n2. Cúi người về trước, tay duỗi thẳng.\n3. Thả lỏng và giữ nhịp thở chậm.",
            equipment = "Bodyweight",
            safetyNotes = "Không ép cơ thể quá mức gây khó chịu."
        ),
        ExerciseDto(
            id = "49",
            name = "Cat-Cow Stretch",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Core",
            category = "Mobility",
            description = "1. Vào tư thế bò, tay dưới vai.\n2. Võng lưng xuống (Cow), sau đó cuộn lưng lên (Cat).\n3. Lặp lại nhịp nhàng theo hơi thở.",
            equipment = "Bodyweight",
            safetyNotes = "Thực hiện chậm để cảm nhận cột sống."
        ),
        ExerciseDto(
            id = "50",
            name = "Shoulder Dislocates",
            imageResId = null,
            localImageName = "incline_dumbbell_press",
            bodyRegion = "Shoulders",
            category = "Mobility",
            description = "1. Cầm dây thun rộng hơn vai.\n2. Nâng tay qua đầu ra sau lưng.\n3. Quay tay về lại phía trước với kiểm soát.",
            equipment = "Resistance Band",
            safetyNotes = "Không kéo quá đau, tăng biên độ từ từ."
        ),
    )

    fun progressSnapshotsForSeed(): List<ProgressSnapshotDto> = listOf(
        ProgressSnapshotDto("2025-03-01", null, 75.0f, 20.0f, 32.4f),
        ProgressSnapshotDto("2025-03-05", null, 74.2f, 19.5f, 32.7f),
        ProgressSnapshotDto("2025-03-10", null, 73.5f, 19.0f, 33.0f),
        ProgressSnapshotDto("2025-03-15", null, 72.8f, 18.6f, 33.2f),
        ProgressSnapshotDto("2025-03-20", null, 72.0f, 18.2f, 33.6f),
    )

    fun nutritionSummaryForSeed(): NutritionSummaryDto =
        NutritionSummaryDto(intake = 2100, burned = 680, goal = 2400)

    fun bodyScanResultForSeed(): BodyScanResultDto =
        BodyScanResultDto(
            bodyComposition = BodyCompositionSectionDto(
                height = "175",
                weight = "68.5",
                totalBodyWater = "38.2",
                protein = "10.1 ",
                mineral = "3.2",
                bodyFatMass = "18.4 ",
                fatFreeMass = "50.1 "
            ),
            muscleFatAnalysis = MuscleFatAnalysisSectionDto(
                weight = MetricWithRangeDto("68.5 ", 68.5f, 40f, 100f),
                skeletalMuscleMass = MetricWithRangeDto("28.3 ", 28.3f, 20f, 45f),
                bodyFatMass = MetricWithRangeDto("18.4 ", 18.4f, 10f, 35f)
            ),
            obesityAnalysis = ObesityAnalysisSectionDto(
                bmi = MetricWithRangeDto("22.4", 22.4f, 15f, 35f),
                percentBodyFat = MetricWithRangeDto("26.8", 26.8f, 10f, 45f)
            ),
            segmentalLean = SegmentalAnalysisSectionDto(
                leftArm = "2.8",
                rightArm = "2.9",
                trunk = "22.1",
                leftLeg = "7.2",
                rightLeg = "7.3"
            ),
            segmentalFat = SegmentalAnalysisSectionDto(
                leftArm = "1.2",
                rightArm = "1.1",
                trunk = "9.8",
                leftLeg = "3.1",
                rightLeg = "3.2 "
            ),
            metabolic = MetabolicSectionDto(
                basalMetabolicRate = "1,520",
                obesityDegree = "98",
                recommendedCalorieIntake = "2,280"
            ),
        )
}
