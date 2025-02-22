'use client'

import { useState } from "react";
import ImageComponent from "./imageComponent";
import { File } from "buffer";

export default function Home() {
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    // todo: place here list of already displayed files and leater fill them 
    // with ready miniatures when processed

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setSelectedFiles(selectedFiles.concat(Array.from(e.target.files)));
        }
    };

    const handleSendFiles = async () => {
        if (selectedFiles) {
            const formData = new FormData();
            for (let i = 0; i < selectedFiles.length; i++) {
                formData.append("files", selectedFiles[i])
            }
            try {
                const response = await fetch("http://localhost:8080/api/upload", {
                    method: "POST",
                    body: formData,
                });

                const data = await response.text();
                console.log("Upload successful", data);
            } catch (error) {
                console.error("Upload failed", error);
            }
        }
    };

    const handleRemoveFile = (indexToRemove: number) => {
        setSelectedFiles((selectedFiles.filter((_, index) => index !== indexToRemove)));
    }

    const getMiniaturesFromServer = () => {
        let images = []
        let files: File[] = [] //todo: get from server logic

        if(!files){
            return <span>"Nothing to display"</span>;
        }

        for (let i = 0; i < files.length; i++){
            images.push(
                ImageComponent(files[i])
            );
        }

        return images;
    }


    return (
        <div className="flex flex-col items-center justify-start h-screen bg-blue-950">
            <h1 className="text-3xl font-semibold mb-8 text-yellow-500 p-3">Image Processor</h1>

            <div className="w-full max-w-4xl bg-sky-700 shadow-lg rounded-lg p-6 space-y-6" >
                <h3 className="text-xl font-medium text-yellow-500">Drop or select files here:</h3>

                {/* File Input Container */}
                <div className="flex items-center justify-center bg-gray-50 border-2 border-dashed border-blue-950 p-6 rounded-md">
                    <input
                        type="file"
                        accept="image/png, image/jpeg"
                        multiple
                        onChange={handleFileChange}
                        className="hidden"
                        id="file-input"
                    />
                    <label
                        htmlFor="file-input"
                        className="cursor-pointer text-center text-blue-500 hover:text-blue-700"
                    >
                        <span className="text-lg">Click to select files</span>
                    </label>
                </div>

                {/* Display selected files */}
                <div className="flex flex-col p-2 space-y-3">
                    <span className="text-lg text-yellow-500 p-2">Current package:</span>

                    <div className="flex flex-wrap gap-4 mt-4">
                        {selectedFiles &&
                            Array.from(selectedFiles).map((file, index) => (
                                <div
                                    key={index}
                                    className="flex items-center justify-center gap-5 bg-emerald-700 p-4 rounded-md shadow-sm w-100"
                                >
                                    <span>{file.name.length < 20 ? file.name : file.name.slice(0, 20) + "... "}</span>
                                    <span>{(file.size / 1024).toFixed(2)} KB </span>
                                    <button className="rounded-md bg-red-400 shadow-sm p-2" onClick={() => handleRemoveFile(index)}>Remove</button>
                                </div>
                            ))}
                    </div>
                </div>



                {/* Send Button */}
                <div className="flex justify-center mt-6">
                    <button
                        onClick={handleSendFiles}
                        className="bg-blue-600 text-white px-8 py-4 rounded-md text-lg hover:bg-blue-700"
                    >
                        Send Files
                    </button>
                </div>
            </div>
            {/* Files got from server */}
            <div className="flex flex-col p-2 space-y-3 bg-blue-800" id="miniatures">
                    {getMiniaturesFromServer()}
            </div>

        </div >
    );
}
