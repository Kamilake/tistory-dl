import os


def print_files_in_dir(root_dir, prefix):
    files = os.listdir(root_dir)
    for file in files:
        path = os.path.join(root_dir, file)
        print(prefix + path)


def changeFolderNameIntToChar(root):
    metadata = open(root + "/Metadata.txt", "r", encoding="UTF-8")
    metadata.readline()
    title = metadata.readline()
    return title.replace("\"", "＂").replace("\\", "＼").replace(":", "：").replace("\"", "＂").replace("/", "／").replace("|", "｜").replace("*", "＊").replace("?", "？").replace("<", "＜").replace(">", "＞").replace("\n", "")


# public static void int main(void) {
print_files_in_dir("./", "블로그 이름: ")
print('Backup 폴더에서 실행해주세요!\n이 스크립트는 폴더를 보기 편하게 숫자에서 게시글 제목으로 바꿔줍니다.')
print('블로그 이름을 입력하세요: ', end="")

root_dir = "./"+input()
# print_files_in_dir(root_dir, "")
for (root, dirs, files) in os.walk(root_dir):
    print("# root : " + root)
    if(len(dirs) > 0):
        for dir_name in dirs:
            # print("dir: " + dir_name)
            i12211 = 1

    if (len(files) > 0):
        for file_name in files:
            # print("file: " + file_name)
            if(file_name == "Metadata.txt"):
                if(root.find(" - ") != -1):
                    print("[스킵]", root)
                else:
                    print(root, '=>', root, "-",
                          changeFolderNameIntToChar(root))
                    os.rename(root, root+" - "+changeFolderNameIntToChar(root))
