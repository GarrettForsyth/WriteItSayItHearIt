package com.example.android.writeitsayithearit.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.android.writeitsayithearit.model.comment.Comment
import com.example.android.writeitsayithearit.model.cue.Cue
import com.example.android.writeitsayithearit.model.story.Story

@Dao
abstract class CommentDao {

    @Insert
    abstract fun insert(comment: Comment)

    @Insert
    abstract fun insert(comments: List<Comment>)

    @Query("SELECT * from comments WHERE id = :id")
    abstract fun comment(id: Int): LiveData<Comment>

    @RawQuery(observedEntities = [ Comment::class ])
    abstract fun comments(query: SupportSQLiteQuery): DataSource.Factory<Int, Comment>

//    @Query("SELECT * from comments WHERE parent_id = :id")
//    abstract fun childComments(id: Int): DataSource.Factory<Int, Comment>

}

