package com.example.masterdetailexample

import com.example.masterdetailexample.detail.DetailPresenter
import com.example.masterdetailexample.detail.models.DetailAction
import com.example.masterdetailexample.detail.models.DetailEvent
import com.example.masterdetailexample.detail.models.DetailResult
import com.example.masterdetailexample.detail.models.DetailState
import com.example.masterdetailexample.room.Item
import com.example.masterdetailexample.room.ItemDao
import io.reactivex.Flowable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class DetailPresenterTest {

    private val itemDao = Mockito.mock(ItemDao::class.java)

    @Before
    fun setup() {
        //Force all schedulers to use the thread the tests are running on.
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        Mockito.`when`(itemDao.getSelectedItem()).thenReturn(Flowable.fromArray(emptyList()))
    }

    @Test
    fun testActionOutput() {

        val testId = "0"
        val testText = "whatever"

        val events = PublishSubject.create<DetailEvent>()
        val actionObserver = TestObserver<DetailAction>()

        val presenter = DetailPresenter(itemDao)
        presenter.attachEventStream(events)
        presenter.actions().subscribe(actionObserver)

        //Check that default state of actions is appropriate
        actionObserver.assertNotComplete()
        actionObserver.assertNoErrors()
        actionObserver.assertValueCount(0)

        //Send all possible event types into events
        events.onNext(DetailEvent.EditItem)
        events.onNext(DetailEvent.DeleteItem(testId))
        events.onNext(DetailEvent.SaveItem(testId, testText))

        //Ensure the output contains the appropriate types and values in the appropriate order.
        actionObserver.assertValues(
                DetailAction.EditItem,
                DetailAction.DeleteItem(testId),
                DetailAction.SaveItem(testId, testText)
        )
    }

    @Test
    fun testStateOutput() {

        val results = PublishSubject.create<DetailResult>()
        val stateObserver = TestObserver<DetailState>()

        val item = Item(0L, "test", false)

        val presenter = DetailPresenter(itemDao)
        presenter.attachResultStream(results)
        presenter.states().subscribe(stateObserver)

        stateObserver.assertNotComplete()
        stateObserver.assertNoErrors()
        stateObserver.assertValueCount(1)

        results.onNext(DetailResult.BeginEditing())
        results.onNext(DetailResult.ChangeInProgress())
        results.onNext(DetailResult.ItemChanged(item))
        results.onNext(DetailResult.ItemRemoved())

        stateObserver.assertValues(
                DetailState.idle(),
                DetailState(null, true, true),
                DetailState(null, false, false),
                DetailState(item, false, true),
                DetailState(null, false, false)
        )
    }
}