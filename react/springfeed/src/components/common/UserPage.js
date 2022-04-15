import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import {useParams} from 'react-router-dom'
import Loading from './Loading'
import FollowList from '../common/FollowList'

import {InfiniteScroll } from 'react-infinite-scroller'

import { Modal } from 'react-bootstrap'

import {FaUserSlash as NoUserIcon} from 'react-icons/fa'

const UserPage = ({setSearchKey, setIsSelectOpen}) => {
    const {id} = useParams()

    const [isLoading, setIsLoading] = useState(false)

    const [posts, setPosts] = useState(null)
    const [savedPosts, setSavedPosts] = useState(null)

    const [introduction, setIntroduction] = useState(null)
    
    const [postCount, setPostCount] = useState(0)
    const [followerCount, setFollowerCount] = useState(0)
    const [followingCount, setFollowingCount] = useState(0)
    
    const [isPostSelected, setIsPostSelected] = useState(true)
    const [isFollowing, setIsFollowing] = useState(false)

    const [followingList, setFollowingList] = useState(null)
    const [isFollowingListOpen, setIsFollowingListOpen] = useState(false)
    const [followingListIndex, setFollowingListIndex] = useState(0)
    const [hasFollowingMore, setHasFollowingMore] = useState(true)
    const loadFollowingList = () => {
        setFollowingListIndex(followingListIndex + 1)
        fetch('/api/user/following?id=' + id + '&page=' + followingListIndex)
            .then((res) => {
                return res.json()
            })
            .then((data) => {
                if(data == null || data == '') {
                    hasFollowingMore(false)
                    toast.error('더 이상 팔로잉이 없어요')
                } else {
                    setFollowingList([...followingList, ...data])
                }
            })
            .catch((err) => {
                setHasFollowingMore(false)
                toast.err('팔로잉 목록을 불러올 수 없어요')
            })
    }
    

    const [followerList, setFollowerList]= useState(null)
    const [isFollowerListOpen, setIsFollowerListOpen] = useState(false)


    const openFollowingList = () => {
        // Fetch Following List
        fetch('/api/user/following?id=' + id)
            .then((res) => {
                return res.json()
            })
            .then((data) => {
                setFollowingList(data)
            })
            .catch((err) => {
                toast.err('팔로잉 목록을 불러오지 못 했어요')
            })
            .finally(() => {
                setIsFollowingListOpen(true)
            })

        // Close Follower List
        closeFollowerList()
    }
    const openFollowerList = () => {
        // Fetch Follower List
        fetch('/api/user/follower?id=' + id)
            .then((res) => {
                return res.json()
            })
            .then((data) => {
                setFollowerList(data)
            })
            .catch((err) => {
                toast.err('팔로워 목록을 불러오지 못 했어요')
            })
            .finally(() => {
                setIsFollowerListOpen(true)
            })

        // Close Following List
        closeFollowingList()
    }
    const closeFollowingList = () => {
        setIsFollowingListOpen(false)
    }
    const closeFollowerList = () => {
        setIsFollowerListOpen(false)
    }

    const [isUserExist, setIsUserExist] = useState(true)

    useEffect(() => {
        setSearchKey('')
        setIsSelectOpen(false)
        
        // Get user introduction, isFollowing
        fetch('/api/user?id=' + id)
            .then((res) => {
                return res.json()
            })
            .then((data) => {
                setIntroduction(data.introduce)
                setIsFollowing(data.isFollowing == 0 ? false : true)
            })
            .catch((err) => {
                toast.error('자기소개를 불러올 수 없었어요')
                setIsUserExist(false)
            })

        // Get follower count
        fetch('/api/user/follow/count?id=' + id)
            .then((res) => {
                return res.json()
            })
            .then((data) => {
                setFollowerCount(data.follower)
                setFollowingCount(data.following)
            })
            .catch((err) => {
                toast.err('팔로워를 불러올 수 없었어요')
            })


        // Get posts initially
        setIsLoading(true)
        fetch('/api/post?userid=' + id)
            .then((res) => {
                return res.json()
            })
            .then((data) => {
                setPosts(data)
                setPostCount(data.length)
            })
            .catch((err) => {
                toast.err('포스트를 불러올 수 없어요')
            })
            .finally(() => {
                setIsLoading(false)
            })

        // Get whether loginUser is following current userPage's user

    }, [])

    useEffect(() => {
        if(savedPosts == null) {
            setIsLoading(true)
            // Fetch saved posts
            fetch('/api/post/save/list?id=' + id)
                .then((res) => {
                    return res.json()
                })
                .then((data) => {
                    setSavedPosts(data)
                })
                .catch((err) => {
                    toast.error(err)
                })
                .finally(() => {
                    setIsLoading(false)
                })
        }
    }, [isPostSelected])

    // Get saved post only when requested


    const handleFollow = () => {
        // fetch POST

        // Toggle UI
        setIsFollowing(!isFollowing)
    }

  return (
    <div className='mt-5'>

        {
            !isUserExist ?
            <NoUserPage/>
            :
            <>
                <div className="row border-bottom">
                    <div className="col-4">
                        <div className="row justify-content-center">
                            <img src="http://picsum.photos/100/100" alt="" className="rounded-circle" 
                                style={{width: '150px'}}
                            />
                        </div>
                    </div>

                    <div className="col-8">
                        <div className="row p-3">
                            <div className="col-6">
                                <div className="h3">
                                    {id}
                                </div>
                            </div>
                            
                            <div className="col-3">
                                <div className="btn btn-danger w-100">
                                    신고하기
                                </div>
                            </div>

                            <div className="col-3" onClick={ handleFollow }>
                                {
                                    isFollowing ?
                                    <div className="btn btn-warning w-100">
                                        언팔로우
                                    </div>
                                    :
                                    <div className="btn btn-success w-100">
                                        팔로우
                                    </div>
                                }
                            </div>
                            
                        </div>

                        <div className="row p-3">
                            <div className="col-4">{postCount} posts</div>

                            <div href='' className="col-4">
                                <div className="btn p-0" onClick={openFollowerList}>
                                    {followerCount} followers
                                </div>
                            </div>

                            <div className="col-4">
                                <div className="btn p-0" onClick={openFollowingList}>
                                    {followingCount} following
                                </div>
                                
                            </div>
                        </div>

                        <div className="row p-4">
                            {introduction}
                        </div>
                    </div>
                </div>

                <div className="row justify-content-center mt-3">
                    <div className="col-3">
                        <div className={"btn btn-outline-primary w-100" + (isPostSelected ? ' active' : '')}
                            onClick={() => {
                                setIsPostSelected(true)
                            }}
                        >POSTS</div>
                    </div>
                    <div className="col-3">
                        <div className={"btn btn-outline-primary w-100" + (!isPostSelected ? ' active' : '')}
                            onClick={() => {
                                setIsPostSelected(false)
                            }}
                        >SAVED</div>
                    </div>
                </div>

                <div className="row mt-4 mb-5">
                    {
                        isLoading ? 
                        <Loading message='포스트를 불러오고 있어요...'/>
                        :
                        isPostSelected ?

                            posts == null || posts.length == 0 ?
                            <div className="h1 text-center mt-5">포스트가 없어요!</div>
                            :
                            posts.map((post) => {
                                return(
                                    <div className="col-4 mb-3">
                                        <img src={post.img} alt="POST IMAGE"/>                                        
                                    </div>
                                )
                            })
                        :
                            savedPosts == null || savedPosts.length == 0 ?
                            <div className="h1 text-center mt-5">저장된 포스트가 없어요!</div>
                            :
                            savedPosts.map((savedPost) => {
                                <div className="col-4 mb-3">
                                    <img src={savedPost.img} alt="SAVED POST IMAGE" />
                                </div>
                            })

                    }
                </div>
            </>
        }

        <Modal show={isFollowingListOpen} onHide={closeFollowingList} className='mt-5'>
            <div className="card">
                <div className="card-header">
                    <div className="h5 text-center">
                        {id}님이 팔로우하고 있는 유저
                    </div>
                </div>
                <div className="card-body">
                    <div className="card-content">
                        <FollowList followList={followingList}/>
                    </div>
                </div>
            </div>
        </Modal>

        <Modal show={isFollowerListOpen} onHide={closeFollowerList} className='mt-5'>
            <div className="card">
                <div className="card-header">
                    <div className="h5 text-center">
                        {id}님을 팔로잉하고 있는 유저
                    </div>

                    <div className="card-body">
                        <div className="card-content">
                            <FollowList followList={followerList}/>
                        </div>
                    </div>
                </div>
            </div>
        </Modal>
    </div>
  )
}


const NoUserPage = () => {
    return(
        <>
            <div className="h1 text-center mb-5">
                <div className="row text-danger h1 mb-5">
                    <NoUserIcon/>
                </div>
                유저가 없어요!
            </div>
        </>
    )
}

export default UserPage