import React, { useEffect, useState } from 'react'

const UserEdit = () => {
    const [jspElement, setJspElement] = useState(null);

    const createMarkup = (data) => {
        return {__html: data}
    }

    useEffect(() => {
        fetch('/user/edit/form')
            .then((res) => {
                return res.text()
            })
            .then((html) => {
                setJspElement(html)
            })
    }, [])

  return (
    <div dangerouslySetInnerHTML={{__html: jspElement}}/>
  )
}

export default UserEdit