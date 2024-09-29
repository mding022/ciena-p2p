import os
import sys

def combine_chunks(input_folder, output_file):
    chunk_number = 0
    with open(output_file, 'wb') as f:
        while True:
            chunk_filename = os.path.join(input_folder, f'chunk_{chunk_number}.bin')
            try:
                with open(chunk_filename, 'rb') as chunk_file:
                    f.write(chunk_file.read())
                chunk_number += 1
            except FileNotFoundError:
                break
    print(f'Chunks have been combined into "{output_file}".')

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python combine_file.py <input_folder> <output_file>")
        sys.exit(1)
    
    input_folder = sys.argv[1]
    output_file = sys.argv[2]
    combine_chunks(input_folder, output_file)
