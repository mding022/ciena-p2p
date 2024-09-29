import os

class CookieBreaker:
    def __init__(self, chunk_size=512):
        self.chunk_size = chunk_size  # Default chunk size is 512 bytes

    def break_cookie(self, file_path, target_directory):
        cookie_pieces = []  # List to hold paths of broken pieces

        # Ensure the target directory exists
        if not os.path.exists(target_directory):
            os.makedirs(target_directory)

        with open(file_path, 'rb') as cookie_file:  
            chunk_number = 0
            while True:
                bite = cookie_file.read(self.chunk_size)  # Read chunk_size bytes at a time
                if not bite:  # If there’s no more to read, stop
                    break
                
                piece_name = f'cookie_piece_{chunk_number}'  # Name each piece
                
                # Create the full path to save the chunk in the target directory
                piece_path = os.path.join(target_directory, piece_name)

                with open(piece_path, 'wb') as chunk:  # Write the chunk to a new file
                    chunk.write(bite)  # Save the chunk
                
                cookie_pieces.append(piece_path)  # Keep track of the chunk paths
                chunk_number += 1  # Increment for the next piece

        return cookie_pieces  # Return the list of broken pieces






# Example usage:
if __name__ == '__main__':
    cookie_breaker = CookieBreaker()  # Create an instance of CookieBreaker
    cookie_path = '/Users/adityabaindur/Desktop/HTH/hackthehill/server/test_file.txt'
    target_directory = '/Users/adityabaindur/Desktop/HTH/hackthehill/server/Build&Rebuild_main_code/Breaking_the_file/broken_pieces'

    cookie_pieces = cookie_breaker.break_cookie(cookie_path, target_directory)

    # Print each broken piece in a cleaner format
    print(f"Cookie broken into {len(cookie_pieces)} pieces:")
    for piece in cookie_pieces:
        print(f"- {piece}")
