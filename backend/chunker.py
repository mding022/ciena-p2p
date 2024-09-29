import argparse
import os

def chunk_file(input_file, output_folder, chunk_size=512):
    os.makedirs(output_folder, exist_ok=True)
    with open(input_file, 'rb') as f:
        chunk_number = 0
        while True:
            chunk = f.read(chunk_size)
            if not chunk:
                break
            chunk_filename = os.path.join(output_folder, f'chunk_{chunk_number}.bin')
            with open(chunk_filename, 'wb') as chunk_file:
                chunk_file.write(chunk)
            chunk_number += 1
    print(f'File "{input_file}" has been chunked into {chunk_number} parts in "{output_folder}".')

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Chunk a file into specified byte sizes.')
    parser.add_argument('input_file', type=str, help='The path of the file to chunk.')
    parser.add_argument('output_folder', type=str, help='The path to the output folder for chunks.')
    parser.add_argument('--chunk_size', type=int, default=512, help='The size of each chunk in bytes (default: 512).')

    args = parser.parse_args()
    chunk_file(args.input_file, args.output_folder, args.chunk_size)
