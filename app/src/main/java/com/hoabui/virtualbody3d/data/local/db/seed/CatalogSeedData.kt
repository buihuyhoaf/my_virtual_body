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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
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
            localImageName = "body_unsplash",
            bodyRegion = "Legs",
            category = "Mobility",
            description = "1. Bước một chân dài lên phía trước (Lunge).\n2. Đặt tay đối diện xuống sàn, tay còn lại xoay hướng lên trời.\n3. Giữ 2-3 giây rồi thực hiện đổi bên.",
            equipment = "Bodyweight",
            safetyNotes = "Giữ đầu gối chân trước thẳng hàng với cổ chân."
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
