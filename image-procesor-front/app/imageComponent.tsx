export default function ImageComponent(image: File){

    const fileURL = URL.createObjectURL(image);

    return(
        <div className="bg-black p-3 rounded-md">
            <img src={fileURL}/>
        </div>
    )

}