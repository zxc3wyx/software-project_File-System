# 北航软件工程结对项目

## 摘要

一个**带用户和用户组的文件管理系统**。

## 内容

### 基本要求

- 输入一律在标准输入流中进行，输出一律在标准输出流中进行。
- 输入内容以指令的形式输入，**一条指令仅占一行**。输出严格遵守指令说明规范，且若无特殊说明（如该指令无输出，或该指令会输出多行），一条指令对应的输出占一行。

### 指令格式

#### 指令大纲

- [基本声明](#基本声明)
- [目录与文件管理 (更新)](#目录与文件管理-更新)
  - [创建目录 mkdir](#创建目录-mkdir)
  - [查看文件或目录信息 info](#查看文件或目录信息-info)
- [目录与文件管理 (扩展)](#目录与文件管理-扩展)
  - [软链接 ln -s](#软链接-ln-s)
  - [硬链接 ln](#硬链接-ln)
  - [文件和目录的移动 mv](#文件和目录的移动-mv)
  - [文件和目录的复制 cp](#文件和目录的复制-cp)
  - [链接真实路径读取 readlink](#链接真实路径读取-readlink)
- [用户和用户组管理](#用户和用户组管理)
  - [用户切换 su](#用户切换-su)
  - [用户返回 exit](#用户返回-exit)
  - [用户查看 whoami](#用户查看-whoami)
  - [用户添加 useradd](#用户添加-useradd)
  - [用户修改 usermod](#用户修改-usermod)
  - [用户删除 userdel](#用户删除-userdel)
  - [用户组添加 groupadd](#用户组添加-groupadd)
  - [用户组删除 groupdel](#用户组删除-groupdel)

#### 基本声明

- 关于文件和目录
  - **路径约束**：文件名结尾的路径后不能添加 `/`，目录名结尾的路径后可以添加 `/`，即：当 `x1` 为文件时，应将形如 `x1/` 这样的路径视为不合法路径，程序需抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的不合法路径。；当 `x1` 为目录时，`x1/`  这样的路径合法且与 `x1` 的含义等价。
  - **软链接和硬链接** 当用户需要在不同的位置使用相同的文件时，它们无需在每个位置都放一个相同的文件，而只要在不同位置中建立同一个文件的软链接或硬链接即可。
  - **链接的本质** 软硬链接本质上都是文件（链接文件），它们可以自动保持每一处的同步性。也即是，用户对任意链接文件的读或写，均可视作在其最终指向的文件或目录上读或写。
  - **链接大小** 软链接文件的大小为 `0` ，硬链接文件大小与其指向的文件大小一致。
  - **目录大小(更新)** 不包含任何文件和子目录的空目录大小为 `0`，非空目录的大小由它所包含的所有文件（包括链接文件）和子目录的大小总和计算得出。
  - **目录修改(更新)** 当目录中的子目录或文件数量发生变化时，该目录被视作修改一次。
  
- 关于用户、用户组管理
  - **用户和用户组** 二者之间是多对多的关系。用户组分为主组和附加组，一个用户可以属于多个用户组，但是用户能且只能属于一个主组，系统在创建用户时会自动创建一个同名的用户组作为该用户的主组。程序也支持创建一个空用户组，然后令已有的用户加入它。用户名和用户组名分别是用户和用户组的唯一标识。
  - **用户名命名规范** 用户名由大小写字母（`a-zA-Z`），英文点（`.`）和下划线（`_`）这三类字符组成，用户名非空且长度不可超过128个字符。
  - **用户组名命名规范** 用户组名由大小写字母（`a-zA-Z`），英文点（`.`）和下划线（`_`）这三类字符组成，用户组名非空且长度不可超过128个字符。
  - **root用户** root用户是文件系统的初始用户，其主组是名为root的用户组，且默认已创建。
- 关于项目
  - 所有的指令动作都应当是原子的。即，一条指令要么成功执行完成动作，要么抛出异常，**不会出现既抛出异常，又会完成部分指令动作的情况**。
  - 所有抛出的继承于官方包预定义的异常类都会被官方包处理，并打印该异常类所带的消息( `message`)。**任何抛出的自定义异常均不会被官方包捕获**，请同学们谨慎使用自定义异常，尽量通过继承官方包提供的异常抽象类来抛出。

#### 目录与文件管理 (更新)

##### 创建目录 mkdir

- **格式**：`mkdir <dstpath>`
- **举例**：当前用户目录在 `/home/buaase`，输入指令 `mkdir homework1`
- **Ubuntu对照指令**：`mkdir`
- **说明**：
  - 若目录 `<dstpath>` 存在，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为指令中的 `<dstpath>`。
  - 若目录 `<dstpath>` 不存在，则创建它，并输出 `Make directory x`，其中 `x` 是 `<dstpath>` 对应的绝对路径。此例中，指令成功执行后会输出 `Make directory /home/buaase/homework1`。
  - 注意，**在任何情况下同一路径下均不可以存在同名的文件或目录**，即若 `<dstpath>` 匹配到一个已经存在的文件，程序需抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<dstpath>`。此条描述对于其它需要创建文件，目录和链接的指令也适用，在下述指令的说明中不再赘述。
  - 注意，`<dstpath>` 的**目录名需遵循目录命名规范**。若它违反，程序应抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<dstpath>`。此条描述对于其它需要创建目录的指令也适用，在下述指令的说明中不再赘述。
  - 注意，`<dstpath>` 的**所有上层目录都需要存在**。若任一上层目录不存在，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为`Path x is invalid`，其中`x`为指令中的 `<dstpath>`。此条描述对于其它需要以非递归方式创建文件，目录和链接指令中的 `<srcpath>` 和 `<dstpath>` 也适用，在下述指令的说明中不再赘述。

##### 查看文件或目录信息 info

- **格式**：`info <dstpath>`
- **举例**：输入指令 `info file.txt`
- **Ubuntu对照指令**：`stat`
- **说明**：

  - 若文件或目录 `<dstpath>` 存在，则输出形如 `create_user create_group create_time modify_time size count absolute_path` 的信息，每个参数由1个空格(` `)分隔。本例中，一个合理的输出可以是 `buaase_user buaase_user 10 20 24 3 /home/buaase`。各参数具体的物理意义参见下表:

|     参数      |                           物理意义                           |
| :-----------: | :--------------------------------------------------------: |
|  create_user  |                 创建 `<dstpath>` 的用户名。                  |
| create_group  |                创建 `<dstpath>` 的用户组名。                 |
|  create_time  |      创建 `<dstpath>` 的指令在输入指令流中的**序号**。       |
|  modify_time  |    最后修改 `<dstpath>` 的指令在输入指令流中的**序号**。     |
|     size      |   为文件或目录 `<dstpath>` 的大小，计算规则参考基本声明。    |
|     count     | 为文件或目录 `<dstpath>` 下各项的个数。对于文件 `<dstpath>` 来说，该值恒为 `1`。对于目录 `<dstpath>` 来说，该值由 `<dstpath>` 的文件和子目录个数总和（包括链接文件在内）计算得出，空目录该值为 `0` 。例如，目录 `a` 仅包含子目录 `b`、普通文件 `file.txt` 和软链接文件 `a_link`，则不论 `b` 中包括几个文件，`a` 目录的 count 值都为3。 |
| absolute_path |            为文件或目录 `<dstpath>` 的绝对路径。             |

  - 若文件或目录 `<dstpath>` 不存在，则抛出抽象类 `FileSystemException` 的继承子类，异常中的消息为 `Path x is invalid`，其中 x 为指令中的 `<dstpath>`。
  - 其它可能的异常情况请根据第一次指导书确定。

#### 目录与文件管理 (扩展)

##### 软链接 ln -s

- **格式**：`ln -s <srcpath> <dstpath>`

- **举例**：当前用户目录在 `/home`，输入指令 `ln -s ../source target`

- **Ubuntu对照指令**：`ln -s`

- **说明**：
  
  - 若文件或目录 `<srcpath>` 不存在，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<srcpath>`。此条描述对于其它用到文件或目录 `<srcpath>` 的指令也适用，在下述指令的说明中不再赘述。
  - 若文件或目录 `<srcpath>` 存在:
     - 当文件或目录 `<dstpath>` 不存在，则在 `<dstpath>` 创建一个软链接文件，该文件指向文件或目录 `<srcpath>` 对应的绝对路径，不输出任何信息。本例中，若 `target` 不存在，则创建一个名为 `target` 的软链接文件，并指向 `/source`。
     - 当文件或目录 `<dstpath>` 与 `<srcpath>` 实际指向的路径一样，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<dstpath>`。此条描述对于 `ln`, `mv`, `cp` 也适用，在下述指令的说明中不再赘述。
     - 当文件 `<dstpath>` 存在，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为指令中的 `<dstpath>`。
     - 若目录 `<dstpath>` 存在，且 `<srcpath>` 为 `<dstpath>` 的上层目录，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<dstpath>`。此条描述对于 `mv`, `cp` 也适用，在下述指令的说明中不再赘述。
     - 当目录 `<dstpath>` 存在，且它没有一个子文件或子目录名字与 `<srcpath>` 对应的绝对路径的文件名或目录名 (下记作 `<srcname>`，根目录的 `<srcname>` 为 `/` )重复，则按照上条描述方法创建一个从 `<dstpath>/<srcname>` 到 `<srcpath>` 绝对路径的软链接文件，不输出任何信息。本例中，若 `target` 是一个已存在的目录，则在该目录下创建一个名为 `source` 的软链接文件，并指向 `/source`。
     - 当目录 `<dstpath>` 存在，且它有一个名为 `<srcname>` 的文件或子目录，程序需要抛出抽象类 `FileSystemException`的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为 `<dstpath>/<srcname>`。
  - 注意，当 `<srcpath>` 指向一个链接文件，或该路径的某一上层目录为软链接时，程序需要将 `<dstpath>` 指向 `<srcpath>` 所指向的文件或目录的绝对路径。此条描述对于 `ln` 也适用，在下述指令的说明中不再赘述。
  - 注意，**默认情况下程序需将所有出现的软链接视为它指向的路径下的文件或目录**，但在以下情况时程序要特殊处理:
     - 当软链接指向一个文件时，指令 `rm`, `info`, `mv`, `cp` 中的软链接路径不做重定向。
     - 当软链接指向一个目录时，指令 `rm`, `rm -r`, `info`, `mv`, `cp` 中**完全匹配软链接路径的参数不做重定向**，但**软链接路径作为参数的上层目录时需重定向**。例如，若 `/home/target` 是软链接路径，且其链接到 `/home/source` 目录，`rm /home/target` 将删除软链接，但 `rm /home/target/1.txt` 将删除 `/home/source` 下的 `1.txt`。
  - 注意，当某一软链接所指向的文件或目录被删除，即使它对应的链接文件仍然存在，但该软链接视作失效。此时，对于任何将软链接路径视为其指向文件或目录路径的指令，程序均需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为软链接指向文件或目录的绝对路径。**但是，当软链接所指向的文件或目录重新被创建时，只要它对应的链接文件仍然存在，软链接就会自动恢复**。
  
##### 硬链接 ln

- **格式**：`ln <srcpath> <dstpath>`
- **举例**：当前用户目录在 `/home`，输入指令 `ln source target`
- **Ubuntu对照指令**：`ln`
- **说明**：
  - 若文件 `<srcpath>` 不存在，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<srcpath>`。
  - 若文件 `<srcpath>` 存在:
     - 当文件或目录 `<dstpath>` 不存在，则在路径 `<dstpath>` 创建一个硬链接文件，该文件指向文件 `<srcpath>`，不输出任何信息。本例中，若 `target` 不存在，则创建一个名为 `target` 的硬链接文件，并指向文件 `source`。
     - 当文件 `<dstpath>` 存在，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为指令中的 `<dstpath>`。
     - 当目录 `<dstpath>` 存在，且它没有一个文件或子目录名字是 `<srcpath>` 对应的绝对路径的文件名 (下记作 `<srcname>`)，则按照上条描述方法创建一个从 `<dstpath>/<srcname>` 指向 `<srcpath>` 的硬链接文件，不输出任何信息。本例中，若目录 `target` 存在，则在该目录下创建一个名为 `source` 的硬链接文件，并指向 `source` 文件。
     - 当目录 `<dstpath>` 存在，且它有一个名为 `<srcname>` 的文件或子目录，程序需要抛出抽象类 `FileSystemException`的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为 `<dstpath>/<srcname>`。
  - 注意，**默认情况下程序需将所有出现的硬链接视为它指向的文件**，但指令 `rm`, `mv` 中的硬链接路径不做重定向。除此之外，当使用 `info` 查看硬链接时，`absolute_path` 打印为硬链接本身的绝对路径，其它文件参数为硬链接所指向文件的参数。
  - 注意，当某一硬链接所指向的文件被删除，或删除后被重新创建，均对该硬链接不产生任何影响。
  - 其它可能的异常情况请根据上述指令的说明确定。

##### 文件和目录的移动 mv

- **格式**：`mv <srcpath> <dstpath>`
- **举例**：当前用户目录在 `/home`，输入指令 `mv source target`
- **Ubuntu对照指令**：`mv`
- **说明**：
  - 若文件或目录 `<srcpath>` 存在，文件或目录 `<dstpath>` 不存在，则将文件或目录 `<srcpath>` 移动到 `<dstpath>`，并重命名为 `<dstpath>` 中的文件或目录名，不输出任何信息。`<dstpath>` 继承 `<srcpath>` 的所有属性。本例中，若 `/home` 中文件或目录 `source` 存在，且不存在名为 `target` 的文件或目录，则将 `source` 重命名为 `target`，并更新 `target` 的 `modify_time`。
  - 若文件 `<srcpath>` 存在，目录 `<dstpath>` 存在:
     - 若目录 `<dstpath>` 下不存在一个文件或子目录名字与 `<srcpath>` 对应的绝对路径的文件名（下记作 `<srcname>`) 重复，则按照第一条描述方法将文件 `<srcpath>` 移动到 `<dstpath>/<srcname>` ，不输出任何信息。本例中，若`/home`中文件`source`存在，目录`target`存在，`target/`中无名为`source`的文件，则将`source`移动到`target/`。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的文件，则将文件 `<srcpath>` 移动并覆盖文件 `<dstpath>/<srcname>`，视作修改一次文件 `<srcpath>`，不输出任何信息。本例中，若`/home`中文件`source`存在，文件`target/source`存在，则移动`source`到`target/`，覆盖`target/source`的内容。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的子目录，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为 `<dstpath>/<srcname>`。
  - 若文件 `<srcpath>` 存在，文件 `<dstpath>` 存在，则移动文件 `<srcpath>` 覆盖 `<dstpath>`，视作修改一次 `<srcpath>` ，不输出任何信息。
  - 若目录 `<srcpath>` 存在，且为当前工作目录或其上层目录，程序需要抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<srcpath>`。
  - 若目录 `<srcpath>` 存在，目录 `<dstpath>` 存在:
     - 若目录 `<dstpath>` 下不存在一个文件或子目录名字与 `<srcpath>` 对应的绝对路径的目录名（下记作 `<srcname>`，根目录的 `<srcname>` 为 `/` ) 重复，则按照第一条描述方法将目录 `<srcpath>` 移动到 `<dstpath>` 下，视作修改一次目录 `<srcpath>`，不输出任何信息。本例中，若`/home`中目录`source`存在，目录`target`存在，`target/`中无名为`source`的文件或子目录，则移动 `source` 到 `target/` 下。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的空子目录，则移动目录 `<srcpath>` 以覆盖 `<dstpath>/<srcname>`，视作修改一次目录 `<dstpath>/<srcname>`，不输出任何信息。本例中，若`/home`中目录 `source` 存在，目录 `target/source` 存在，且目录 `target/source` 为空目录，则将 `source` 移动到 `target` 下，覆盖 `target/source` 目录。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的文件或非空子目录，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为 `<dstpath>/<srcname>`。
  - 若目录 `<srcpath>` 存在，文件 `<dstpath>` 存在，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为指令中的 `<dstpath>`。
  - 注意，执行此指令若涉及到目录中的文件或子目录，需要递归地更新 `<dstpath>` 下文件或子目录的 `modify_time`。
  - 其它可能的异常情况请根据上述指令的说明确定。

##### 文件和目录的复制 cp

- **格式**：`cp <srcpath> <dstpath>`
- **举例**：当前用户目录在 `/home`，输入指令 `cp source target`
- **Ubuntu对照指令**：`cp，cp -r`
- **说明**：
   - 若文件或目录 `<srcpath>` 存在，文件或目录 `<dstpath>` 不存在，则根据 `<srcpath>` 的文件类型创建 `<dstpath>`，并将 `<srcpath>` 的内容复制到 `<dstpath>`，不输出任何信息。本例中，若 `/home` 中文件 `source` 存在，`target` 不存在，则在 `/home` 下新建 `target` 文件，将 `source` 内容复制到 `target` 中。
  - 若文件 `<srcpath>` 存在，目录 `<dstpath>` 存在:
     - 若目录 `<dstpath>` 下不存在一个文件或子目录名字与 `<srcpath>` 对应的绝对路径的文件名 (下记作 `<srcname>`) 重复，则按照第一条描述方法创建一个 `<dstpath>/<srcname>` 文件，并将 `<srcpath>` 的内容复制到 `<dstpath>/<srcname>`，不输出任何信息。本例中，若 `/home` 目录下存在文件 `source` 和目录 `target`，且 `target` 下不存在名为 `source` 的文件或子目录，则创建 `target/source` 文件，并将文件 `source` 的内容复制到 `target/source`。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的文件，则复制 `<srcpath>` 以覆盖 `<dstpath>/<srcname>` ，视作修改一次 `<dstpath>/<srcname>`，不输出任何信息。本例中，若 `/home` 目录下存在文件 `source` 和 `target/source`，则用 `source` 覆盖 `target/source`，并视作修改了 `target/source`。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的子目录，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为 `<dstpath>/<srcname>`。
  - 若文件 `<srcpath>` 存在，文件 `<dstpath>` 存在，则复制文件 `<srcpath>` 覆盖 `<dstpath>`，视作修改一次 `<dstpath>` ，不输出任何信息。
  - 若目录 `<srcpath>` 存在，目录 `<dstpath>` 存在:
     - 若目录 `<dstpath>` 下不存在一个文件或子目录名字与 `<srcpath>` 对应的绝对路径的目录名 (下记作 `<srcname>`，根目录的 `<srcname>` 为 `/` ) 重复，则按照第一条描述方法创建一个 `<dstpath>/<srcname>` 目录，并将 `<srcpath>` 的内容复制到 `<dstpath>/<srcname>`，不输出任何信息。本例中，若 `/home` 目录下存在目录 `source` 和目录 `target`，且 `target` 下不存在名为 `source` 的文件或子目录，则创建 `target/source` 目录，并将目录 `source` 的内容复制到 `target/source`。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的空子目录，则复制目录 `<srcpath>` 以覆盖 `<dstpath>/<srcname>`，视作修改一次目录 `<dstpath>/<srcname>`，不输出任何信息。本例中，若`/home`中目录 `source` 存在，目录 `target/source` 存在，且目录 `target/source` 为空目录，则将 `source` 复制到 `target/` 下，覆盖 `target/source` 目录。
     - 若目录 `<dstpath>` 下存在一个名为 `<srcname>` 的文件或非空子目录，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为 `<dstpath>/<srcname>`。
  - 若目录 `<srcpath>` 存在，文件 `<dstpath>` 存在，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x exists`，其中 `x` 为指令中的 `<dstpath>`。
  - 注意，从目录 `<srcpath>` 复制到 `<dstpath>` 中的文件和子目录全部视为新创建。
  - 其它可能的异常情况请根据上述指令的说明确定。

##### 链接真实路径读取 readlink

- **格式**：`readlink <dstpath>`
- **举例**：当前用户目录在 `/home`，输入指令 `readlink /target`
- **Ubuntu对照指令**：`readlink`
- **说明**：
  - 若软链接 `<dstpath>` 存在，则输出它指向的文件或目录的绝对路径。本例中，软链接`/target` 指向的目录是 `/home/source`，因此输出 `/home/source`。
  - 若软链接 `<dstpath>` 不存在，则抛出抽象类 `FileSystemException` 的继承子类（自行实现），异常中的消息为 `Path x is invalid`，其中 `x` 为指令中的 `<dstpath>`。
  - 其它可能的异常情况请根据上述指令的说明确定。

#### 用户和用户组管理

##### 用户切换 su

- **格式**：`su <username>`
- **举例**：当前用户为 root，输入指令`su buaase_user`
- **Ubuntu对照指令**：`su`
- **说明**：

  - **只有 root 用户可以执行此命令**,否则程序需抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Operation is not permitted`。此条描述对于 `useradd`, `usermod`, `userdel`, `groupadd`, `groupdel` 指令也适用，在下述指令的说明中不再赘述。
  - 若用户 `<username>` 为 `root`，程序需要抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Operation is not permitted`。此条描述对于其它用到 `<username>` 和 `<groupname>` 的指令也适用，在下述指令的说明中不再赘述。
  - 若用户 `<username>` 不为 `root` 且存在，则当前用户变更为用户 `<username>` ，当前的工作目录不发生变化，且不输出任何信息。
  - 若用户 `<username>` 不存在，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `User x is invalid`，其中 `x` 为指令中的 `<username>`。

##### 用户返回 exit

- **格式**：`exit`
- **举例**：当前用户为 `buaase_user`，输入指令 `exit`
- **Ubuntu对照指令**：`exit`
- **说明**：
  - **只有非root用户可以执行该命令**，否则程序需抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为`Operation is not permitted`。
  - 指令成功执行后，当前用户变更为root，且当前工作目录切换为root用户最后一次执行 `su` 指令时所在的工作目录(如存在，不存在时切换为根目录 `/`)，不输出任何信息。

##### 用户查看 whoami

- **格式**：`whoami`
- **举例**：当前用户为 `buaase_user`，输入指令 `whoami`
- **Ubuntu对照指令**：`whoami`
- **说明**：

  - 指令成功执行后，输出当前用户的用户名。本例中，指令成功运行后输出 `buaase_user`。

##### 用户添加 useradd

- **格式**：`useradd <username>`
- **举例**：当前用户为root用户，输入指令 `useradd buaase_user`
- **Ubuntu对照指令**：`useradd`
- **说明**：
  - 若用户 `<username>` 不存在，则创建一个名字为输入指令中 `<username>` 的用户，不输出任何信息。同时，若与用户 `<username>` 同名的用户组不存在，则创建一个名为 `<username>` 的空用户组，将用户 `<username>` 加入该组，同时把该组作为该用户的主组。但若与用户 `<username>` 同名的用户组已存在，则只将用户 `<username>` 加入该组，同时把该组作为该用户的主组。
  - 若用户 `<username>` 已经存在，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `User x exists`，其中 `x` 为指令中的 `<username>`。
  - 注意，创建用户时，`<username>` 需遵循用户名命名规范。若它违反，程序应抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `User x is invalid`，其中 x 为指令中的 `<username>`。
  - 其它可能的异常情况请根据上述指令的说明确定。

##### 用户修改 usermod

- **格式**：`usermod <groupname> <username>`
- **举例**：当前用户为root用户，输入指令 `usermod buaase_group buaase_user`
- **Ubuntu对照指令**：`usermod`
- **说明**：

  - 若用户组 `<groupname>` 和用户 `<username>` 均存在，且用户 `<username>` 不在用户组 `<groupname>` 中，则将该用户加入该用户组，不输出任何信息。但是，若该用户已在该用户组中，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `User x is invalid`，其中 `x` 为指令中的 `<username>`。
  - 若用户组 `<groupname>` 不存在，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Group x is invalid`，其中 `x` 为指令中的 `<groupname>`。
  - 若用户 `<username>` 不存在，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `User x is invalid`，其中 `x` 为指令中的 `<username>`。
  - 其它可能的异常情况请根据上述指令的说明确定。

##### 用户删除 userdel

- **格式**：`userdel <username>`
- **举例**：当前用户为root用户，输入指令 `userdel buaase_user`
- **Ubuntu对照指令**：`userdel`
- **说明**：

  - 若用户 `<username>` 存在，则删除该用户，并将该用户从所有包含它的组中删除。与此同时，程序需判断 `<username>` 的主组有无其它所属用户。若无，则程序自动删除该主组，否则不删除该组。不输出任何信息。
  - 若用户 `<username>` 不存在，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `User x is invalid`，其中 `x` 为指令中的 `<username>`。
  - 其它可能的异常情况请根据上述指令的说明确定。


##### 用户组添加 groupadd

- **格式**：`groupadd <groupname>`
- **举例**：当前用户为root用户，输入指令 `groupadd buaase_group`
- **Ubuntu对照指令**：`groupadd`
- **说明**：
  - 若用户组 `<groupname>` 与已有的用户组重名，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Group x exists`，其中 `x` 为指令中的 `<groupname>`。
  - 若用户组 `<groupname>` 不存在，则创建一个空用户组 `<groupname>`，不输出任何信息。
  - 注意，创建用户组时，`<groupname>` 需遵循用户组名命名规范。若它违反，程序应抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Group x is invalid`，其中 x 为指令中的 `<groupname>`。
  - 其它可能的异常情况请根据上述指令的说明确定。

##### 用户组删除 groupdel

- **格式**：`groupdel <groupname>`
- **举例**：当前用户为root用户，输入指令 `groupdel buaase_group`
- **Ubuntu对照指令**：`groupdel`
- **说明**：
  - 若用户组 `<groupname>` 是任一用户的主组，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Group x is invalid`，其中 `x` 为指令中的 `<groupname>`。
  - 若用户组 `<groupname>` 存在且不是主组，则删除该用户组，且不输出任何信息。
  - 若用户组 `<groupname>` 不存在，则抛出抽象类 `UserSystemException` 的继承子类（自行实现），异常中的消息为 `Group x is invalid`，其中 `x` 为指令中的 `<groupname>`。
  - 其它可能的异常情况请根据上述指令的说明确定。

