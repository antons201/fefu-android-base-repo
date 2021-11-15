package ru.fefu.activitytracker.activitytracking.train

class TrainTypeCardsRepository {
    private val cardsList = listOf(
        TrainTypeCard(
            TrainTypes.CYCLING,
            true
        ),
        TrainTypeCard(
            TrainTypes.RUNNING,
            false
        ),
        TrainTypeCard(
            TrainTypes.WALKING,
            false
        )
    )

    fun getTrainTypeCards(): List<TrainTypeCard> = cardsList
}